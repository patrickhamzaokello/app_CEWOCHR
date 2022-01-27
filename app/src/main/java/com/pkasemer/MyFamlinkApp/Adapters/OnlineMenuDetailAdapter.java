package com.pkasemer.MyFamlinkApp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.google.android.material.button.MaterialButton;
import com.pkasemer.MyFamlinkApp.Models.SelectedCategoryMenuItemResult;
import com.pkasemer.MyFamlinkApp.MyMenuDetail;
import com.pkasemer.MyFamlinkApp.R;
import com.pkasemer.MyFamlinkApp.RootActivity;
import com.pkasemer.MyFamlinkApp.Utils.GlideApp;
import com.pkasemer.MyFamlinkApp.Utils.MenuDetailListener;
import com.pkasemer.MyFamlinkApp.localDatabase.SenseDBHelper;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Suleiman on 19/10/16.
 */

public class OnlineMenuDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private static final int HERO = 2;

    //    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/w150";
    private static final String BASE_URL_IMG = "";


    private List<SelectedCategoryMenuItemResult> movieSelectedCategoryMenuItemResults;
    private Context context;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    DrawableCrossFadeFactory factory =
            new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();

    private MenuDetailListener mCallback;
    private String errorMsg;


    int minteger = 1;
    int totalPrice;

    SenseDBHelper db;
    boolean food_db_itemchecker;

    public OnlineMenuDetailAdapter(Context context, MenuDetailListener callback) {
        this.context = context;
        this.mCallback = callback;
        movieSelectedCategoryMenuItemResults = new ArrayList<>();
    }

    public List<SelectedCategoryMenuItemResult> getMovies() {
        return movieSelectedCategoryMenuItemResults;
    }

    public void setMovies(List<SelectedCategoryMenuItemResult> movieSelectedCategoryMenuItemResults) {
        this.movieSelectedCategoryMenuItemResults = movieSelectedCategoryMenuItemResults;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case HERO:
                View viewHero = inflater.inflate(R.layout.online_menudetail_hero, parent, false);
                viewHolder = new HeroVH(viewHero);
                break;
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.pagination_item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.online_menudetail_pagination_item_list, parent, false);
        viewHolder = new MovieVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        SelectedCategoryMenuItemResult selectedCategoryMenuItemResult = movieSelectedCategoryMenuItemResults.get(position); // Movie
        db = new SenseDBHelper(context);

        switch (getItemViewType(position)) {
            case HERO:
                final HeroVH heroVh = (HeroVH) holder;


                heroVh.menu_name.setText(selectedCategoryMenuItemResult.getMenuName());
                heroVh.menu_shortinfo.setText(formatYearLabel(selectedCategoryMenuItemResult));
                heroVh.menu_description.setText(selectedCategoryMenuItemResult.getDescription());

                Glide
                        .with(context)
                        .load(BASE_URL_IMG + selectedCategoryMenuItemResult.getMenuImage())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                heroVh.mProgress.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                heroVh.mProgress.setVisibility(View.GONE);
                                return false;
                            }

                        })
                        .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
                        .centerCrop()
                        .transition(withCrossFade(factory))
                        .into(heroVh.menu_image);


                heroVh.ratingnumber.setText(selectedCategoryMenuItemResult.getRating() + " | " + "5");
                heroVh.menu_price.setText(NumberFormat.getNumberInstance(Locale.US).format(selectedCategoryMenuItemResult.getPrice()));
                heroVh.menu_qtn.setText("1");
                heroVh.menu_total_price.setText(NumberFormat.getNumberInstance(Locale.US).format(selectedCategoryMenuItemResult.getPrice()));

                food_db_itemchecker = db.checktweetindb(String.valueOf(selectedCategoryMenuItemResult.getMenuId()));

                updatecartCount();

                if (food_db_itemchecker) {
                    heroVh.btnAddtoCart.setText("Add to Cart");
                    heroVh.btnAddtoCart.setBackgroundColor(ContextCompat.getColor(context, R.color.purple_200));
                    heroVh.btnAddtoCart.setTextColor(ContextCompat.getColor(context, R.color.white));
                    heroVh.itemQuanEt.setText("1");

                    heroVh.menu_detail_st_cartbtn.setBackground(context.getResources().getDrawable(R.drawable.custom_cart_btn));
                    heroVh.menu_detail_st_likebtn.setBackground(context.getResources().getDrawable(R.drawable.custom_cart_like_btn));


                } else {
                    heroVh.btnAddtoCart.setText("Remove from Cart");
                    heroVh.btnAddtoCart.setBackgroundColor(ContextCompat.getColor(context, R.color.buttonRed));
                    heroVh.btnAddtoCart.setTextColor(ContextCompat.getColor(context, R.color.white));

                    minteger = db.getMenuQtn(String.valueOf(selectedCategoryMenuItemResult.getMenuId()));

                    display(minteger,heroVh,selectedCategoryMenuItemResult);

                    heroVh.menu_detail_st_cartbtn.setBackground(context.getResources().getDrawable(R.drawable.custom_cart_btn_done));
                    heroVh.menu_detail_st_likebtn.setBackground(context.getResources().getDrawable(R.drawable.custom_cart_like_btn_done));


                }

                heroVh.incrementQtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        minteger = minteger + 1;
                        display(minteger,heroVh, selectedCategoryMenuItemResult);
                    }
                });

                heroVh.decreaseQtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        minteger = minteger - 1;

                        if (minteger <= 1) {
                            minteger = 1;
                            display(minteger,heroVh,selectedCategoryMenuItemResult);
                        } else {
                            display(minteger,heroVh,selectedCategoryMenuItemResult);
                        }
                    }
                });

                heroVh.btnAddtoCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        food_db_itemchecker = db.checktweetindb(String.valueOf(selectedCategoryMenuItemResult.getMenuId()));


                        if (food_db_itemchecker) {
                            db.addTweet(
                                    selectedCategoryMenuItemResult.getMenuId(),
                                    selectedCategoryMenuItemResult.getMenuName(),
                                    selectedCategoryMenuItemResult.getPrice(),
                                    selectedCategoryMenuItemResult.getDescription(),
                                    selectedCategoryMenuItemResult.getMenuTypeId(),
                                    selectedCategoryMenuItemResult.getMenuImage(),
                                    selectedCategoryMenuItemResult.getBackgroundImage(),
                                    selectedCategoryMenuItemResult.getIngredients(),
                                    selectedCategoryMenuItemResult.getMenuStatus(),
                                    selectedCategoryMenuItemResult.getCreated(),
                                    selectedCategoryMenuItemResult.getModified(),
                                    selectedCategoryMenuItemResult.getRating(),
                                    minteger
                            );


                            heroVh.btnAddtoCart.setText("Remove from Cart");
                            heroVh.btnAddtoCart.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.buttonRed));
                            heroVh.btnAddtoCart.setTextColor(ContextCompat.getColor(v.getContext(), R.color.white));

                            heroVh.menu_detail_st_cartbtn.setBackground(context.getResources().getDrawable(R.drawable.custom_cart_btn_done));
                            heroVh.menu_detail_st_likebtn.setBackground(context.getResources().getDrawable(R.drawable.custom_cart_like_btn_done));


                            updatecartCount();


                        } else {
                            db.deleteTweet(String.valueOf(selectedCategoryMenuItemResult.getMenuId()));

                            heroVh.btnAddtoCart.setText("Add to Cart");
                            heroVh.btnAddtoCart.setBackgroundColor(ContextCompat.getColor(context, R.color.purple_200));
                            heroVh.btnAddtoCart.setTextColor(ContextCompat.getColor(context, R.color.white));


                            heroVh.menu_detail_st_cartbtn.setBackground(context.getResources().getDrawable(R.drawable.custom_cart_btn));
                            heroVh.menu_detail_st_likebtn.setBackground(context.getResources().getDrawable(R.drawable.custom_cart_like_btn));

                            updatecartCount();

                        }
                    }
                });


                heroVh.menu_detail_st_cartbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        food_db_itemchecker = db.checktweetindb(String.valueOf(selectedCategoryMenuItemResult.getMenuId()));


                        if (food_db_itemchecker) {
                            db.addTweet(
                                    selectedCategoryMenuItemResult.getMenuId(),
                                    selectedCategoryMenuItemResult.getMenuName(),
                                    selectedCategoryMenuItemResult.getPrice(),
                                    selectedCategoryMenuItemResult.getDescription(),
                                    selectedCategoryMenuItemResult.getMenuTypeId(),
                                    selectedCategoryMenuItemResult.getMenuImage(),
                                    selectedCategoryMenuItemResult.getBackgroundImage(),
                                    selectedCategoryMenuItemResult.getIngredients(),
                                    selectedCategoryMenuItemResult.getMenuStatus(),
                                    selectedCategoryMenuItemResult.getCreated(),
                                    selectedCategoryMenuItemResult.getModified(),
                                    selectedCategoryMenuItemResult.getRating(),
                                    minteger
                            );


                            heroVh.btnAddtoCart.setText("Remove from Cart");
                            heroVh.btnAddtoCart.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.buttonRed));
                            heroVh.btnAddtoCart.setTextColor(ContextCompat.getColor(v.getContext(), R.color.white));

                            heroVh.menu_detail_st_cartbtn.setBackground(context.getResources().getDrawable(R.drawable.custom_cart_btn_done));
                            heroVh.menu_detail_st_likebtn.setBackground(context.getResources().getDrawable(R.drawable.custom_cart_like_btn_done));

                            updatecartCount();


                        } else {
                            db.deleteTweet(String.valueOf(selectedCategoryMenuItemResult.getMenuId()));

                            heroVh.btnAddtoCart.setText("Add to Cart");
                            heroVh.btnAddtoCart.setBackgroundColor(ContextCompat.getColor(context, R.color.purple_200));
                            heroVh.btnAddtoCart.setTextColor(ContextCompat.getColor(context, R.color.white));


                            heroVh.menu_detail_st_cartbtn.setBackground(context.getResources().getDrawable(R.drawable.custom_cart_btn));
                            heroVh.menu_detail_st_likebtn.setBackground(context.getResources().getDrawable(R.drawable.custom_cart_like_btn));


                            updatecartCount();

                        }
                    }
                });


                heroVh.menu_detail_st_likebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        food_db_itemchecker = db.checktweetindb(String.valueOf(selectedCategoryMenuItemResult.getMenuId()));


                        if (food_db_itemchecker) {
                            db.addTweet(
                                    selectedCategoryMenuItemResult.getMenuId(),
                                    selectedCategoryMenuItemResult.getMenuName(),
                                    selectedCategoryMenuItemResult.getPrice(),
                                    selectedCategoryMenuItemResult.getDescription(),
                                    selectedCategoryMenuItemResult.getMenuTypeId(),
                                    selectedCategoryMenuItemResult.getMenuImage(),
                                    selectedCategoryMenuItemResult.getBackgroundImage(),
                                    selectedCategoryMenuItemResult.getIngredients(),
                                    selectedCategoryMenuItemResult.getMenuStatus(),
                                    selectedCategoryMenuItemResult.getCreated(),
                                    selectedCategoryMenuItemResult.getModified(),
                                    selectedCategoryMenuItemResult.getRating(),
                                    minteger
                            );


                            heroVh.btnAddtoCart.setText("Remove from Cart");
                            heroVh.btnAddtoCart.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.buttonRed));
                            heroVh.btnAddtoCart.setTextColor(ContextCompat.getColor(v.getContext(), R.color.white));

                            heroVh.menu_detail_st_cartbtn.setBackground(context.getResources().getDrawable(R.drawable.custom_cart_btn_done));
                            heroVh.menu_detail_st_likebtn.setBackground(context.getResources().getDrawable(R.drawable.custom_cart_like_btn_done));

                            updatecartCount();


                        } else {
                            db.deleteTweet(String.valueOf(selectedCategoryMenuItemResult.getMenuId()));

                            heroVh.btnAddtoCart.setText("Add to Cart");
                            heroVh.btnAddtoCart.setBackgroundColor(ContextCompat.getColor(context, R.color.purple_200));
                            heroVh.btnAddtoCart.setTextColor(ContextCompat.getColor(context, R.color.white));


                            heroVh.menu_detail_st_cartbtn.setBackground(context.getResources().getDrawable(R.drawable.custom_cart_btn));
                            heroVh.menu_detail_st_likebtn.setBackground(context.getResources().getDrawable(R.drawable.custom_cart_like_btn));


                            updatecartCount();

                        }
                    }
                });


                break;
            case ITEM:
                final MovieVH movieVH = (MovieVH) holder;

                movieVH.mMovieTitle.setText(selectedCategoryMenuItemResult.getMenuName());

                movieVH.mMoviePrice.setText("Ugx " + NumberFormat.getNumberInstance(Locale.US).format(selectedCategoryMenuItemResult.getPrice()));

                movieVH.mYear.setText(
                        "Rating " + selectedCategoryMenuItemResult.getRating()
                                + " | "
                                + "5"
                );
                movieVH.mMovieDesc.setText(selectedCategoryMenuItemResult.getDescription());

                Glide
                        .with(context)
                        .load(BASE_URL_IMG + selectedCategoryMenuItemResult.getMenuImage())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                movieVH.mProgress.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                movieVH.mProgress.setVisibility(View.GONE);
                                return false;
                            }

                        })
                        .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
                        .centerCrop()
                        .transition(withCrossFade(factory))
                        .into(movieVH.mPosterImg);

                //show toast on click of show all button
                movieVH.mPosterImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context.getApplicationContext(), MyMenuDetail.class);
                        //PACK DATA
                        i.putExtra("SENDER_KEY", "MenuDetails");
                        i.putExtra("selectMenuId", selectedCategoryMenuItemResult.getMenuId());
                        i.putExtra("category_selected_key", selectedCategoryMenuItemResult.getMenuTypeId());
                        context.startActivity(i);
                    }
                });

                movieVH.menu_st_carttn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        food_db_itemchecker = db.checktweetindb(String.valueOf(selectedCategoryMenuItemResult.getMenuId()));


                        if (food_db_itemchecker) {
                            db.addTweet(
                                    selectedCategoryMenuItemResult.getMenuId(),
                                    selectedCategoryMenuItemResult.getMenuName(),
                                    selectedCategoryMenuItemResult.getPrice(),
                                    selectedCategoryMenuItemResult.getDescription(),
                                    selectedCategoryMenuItemResult.getMenuTypeId(),
                                    selectedCategoryMenuItemResult.getMenuImage(),
                                    selectedCategoryMenuItemResult.getBackgroundImage(),
                                    selectedCategoryMenuItemResult.getIngredients(),
                                    selectedCategoryMenuItemResult.getMenuStatus(),
                                    selectedCategoryMenuItemResult.getCreated(),
                                    selectedCategoryMenuItemResult.getModified(),
                                    selectedCategoryMenuItemResult.getRating(),
                                    minteger
                            );


                            movieVH.menu_st_carttn.setBackground(context.getResources().getDrawable(R.drawable.custom_cart_btn_done));
                            movieVH.menu_st_likebtn.setBackground(context.getResources().getDrawable(R.drawable.custom_cart_like_btn_done));

                            updatecartCount();


                        } else {
                            db.deleteTweet(String.valueOf(selectedCategoryMenuItemResult.getMenuId()));


                            movieVH.menu_st_carttn.setBackground(context.getResources().getDrawable(R.drawable.custom_cart_btn));
                            movieVH.menu_st_likebtn.setBackground(context.getResources().getDrawable(R.drawable.custom_cart_like_btn));


                            updatecartCount();

                        }
                    }
                });
                movieVH.menu_st_likebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        food_db_itemchecker = db.checktweetindb(String.valueOf(selectedCategoryMenuItemResult.getMenuId()));


                        if (food_db_itemchecker) {
                            db.addTweet(
                                    selectedCategoryMenuItemResult.getMenuId(),
                                    selectedCategoryMenuItemResult.getMenuName(),
                                    selectedCategoryMenuItemResult.getPrice(),
                                    selectedCategoryMenuItemResult.getDescription(),
                                    selectedCategoryMenuItemResult.getMenuTypeId(),
                                    selectedCategoryMenuItemResult.getMenuImage(),
                                    selectedCategoryMenuItemResult.getBackgroundImage(),
                                    selectedCategoryMenuItemResult.getIngredients(),
                                    selectedCategoryMenuItemResult.getMenuStatus(),
                                    selectedCategoryMenuItemResult.getCreated(),
                                    selectedCategoryMenuItemResult.getModified(),
                                    selectedCategoryMenuItemResult.getRating(),
                                    minteger
                            );
                            movieVH.menu_st_carttn.setBackground(context.getResources().getDrawable(R.drawable.custom_cart_btn_done));
                            movieVH.menu_st_likebtn.setBackground(context.getResources().getDrawable(R.drawable.custom_cart_like_btn_done));



                            updatecartCount();


                        } else {
                            db.deleteTweet(String.valueOf(selectedCategoryMenuItemResult.getMenuId()));


                            movieVH.menu_st_carttn.setBackground(context.getResources().getDrawable(R.drawable.custom_cart_btn));
                            movieVH.menu_st_likebtn.setBackground(context.getResources().getDrawable(R.drawable.custom_cart_like_btn));



                            updatecartCount();

                        }
                    }
                });



                break;

            case LOADING:
//                Do nothing
                LoadingVH loadingVH = (LoadingVH) holder;

                if (retryPageLoad) {
                    loadingVH.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingVH.mProgressBar.setVisibility(View.GONE);

                    loadingVH.mErrorTxt.setText(
                            errorMsg != null ?
                                    errorMsg :
                                    context.getString(R.string.error_msg_unknown));

                } else {
                    loadingVH.mErrorLayout.setVisibility(View.GONE);
                    loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                }

                break;
        }

    }

    @Override
    public int getItemCount() {
        return movieSelectedCategoryMenuItemResults == null ? 0 : movieSelectedCategoryMenuItemResults.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HERO;
        } else {
            return (position == movieSelectedCategoryMenuItemResults.size() - 1 && isLoadingAdded) ?
                    LOADING : ITEM;
        }
    }

    public void switchContent(int id, Fragment fragment) {
        if (context == null)
            return;
        if (context instanceof RootActivity) {
            RootActivity mainActivity = (RootActivity) context;
            Fragment frag = fragment;
            mainActivity.switchContent(id, frag, "MenuDetails");
        }

    }




    /*
   Helpers
   _________________________________________________________________________________________________
    */

    private String formatYearLabel(SelectedCategoryMenuItemResult selectedCategoryMenuItemResult) {
        return "Created | " +
                selectedCategoryMenuItemResult.getCreated().substring(0, 4);
    }

    private RequestBuilder<Drawable> loadImage(@NonNull String posterPath) {
        return GlideApp
                .with(context)
                .load(BASE_URL_IMG + posterPath)
                .centerCrop();
    }

    public void add(SelectedCategoryMenuItemResult r) {
        movieSelectedCategoryMenuItemResults.add(r);
        notifyItemInserted(movieSelectedCategoryMenuItemResults.size() - 1);
    }

    public void addAll(List<SelectedCategoryMenuItemResult> moveSelectedCategoryMenuItemResults) {
        for (SelectedCategoryMenuItemResult selectedCategoryMenuItemResult : moveSelectedCategoryMenuItemResults) {
            add(selectedCategoryMenuItemResult);
        }
    }

    public void remove(SelectedCategoryMenuItemResult r) {
        int position = movieSelectedCategoryMenuItemResults.indexOf(r);
        if (position > -1) {
            movieSelectedCategoryMenuItemResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new SelectedCategoryMenuItemResult());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = movieSelectedCategoryMenuItemResults.size() - 1;
        SelectedCategoryMenuItemResult selectedCategoryMenuItemResult = getItem(position);

        if (selectedCategoryMenuItemResult != null) {
            movieSelectedCategoryMenuItemResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(movieSelectedCategoryMenuItemResults.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }

    public SelectedCategoryMenuItemResult getItem(int position) {
        return movieSelectedCategoryMenuItemResults.get(position);
    }


    private void updatecartCount() {
        db = new SenseDBHelper(context);
        String mycartcount = String.valueOf(db.countCart());
        Intent intent = new Intent(context.getString(R.string.cartcoutAction));
        intent.putExtra(context.getString(R.string.cartCount), mycartcount);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }


    private void display(int number,HeroVH heroVh , SelectedCategoryMenuItemResult selectedCategoryMenuItemResult) {

        totalPrice = number * (selectedCategoryMenuItemResult.getPrice());

        heroVh.itemQuanEt.setText("" + number);
        heroVh.menu_total_price.setText(NumberFormat.getNumberInstance(Locale.US).format(totalPrice));

        food_db_itemchecker = db.checktweetindb(String.valueOf(selectedCategoryMenuItemResult.getMenuId()));

        if (food_db_itemchecker) {
            return;
            //item doesnt exist
        } else {
            db.updateMenuCount(number,selectedCategoryMenuItemResult.getMenuId());
            //item exists

        }
    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */

    protected class HeroVH extends RecyclerView.ViewHolder {
        private ImageView menu_image;
        private TextView menu_name, menu_shortinfo, menu_description, ratingnumber, menu_price, menu_qtn, itemQuanEt, menu_total_price;
        private Button incrementQtn, decreaseQtn, btnAddtoCart, menu_detail_st_cartbtn, menu_detail_st_likebtn;
        private ProgressBar mProgress;


        public HeroVH(View itemView) {
            super(itemView);
            // init views
            menu_image = (ImageView) itemView.findViewById(R.id.menu_image);
            menu_name = (TextView) itemView.findViewById(R.id.menu_name);
            menu_shortinfo = (TextView) itemView.findViewById(R.id.menu_shortinfo);
            menu_description = (TextView) itemView.findViewById(R.id.menu_description);
            ratingnumber = (TextView) itemView.findViewById(R.id.ratingnumber);
            menu_price = (TextView) itemView.findViewById(R.id.menu_price);
            menu_qtn = (TextView) itemView.findViewById(R.id.menu_qtn);
            itemQuanEt = (TextView) itemView.findViewById(R.id.itemQuanEt);
            menu_total_price = (TextView) itemView.findViewById(R.id.menu_total_price);
            incrementQtn = itemView.findViewById(R.id.addBtn);
            decreaseQtn = itemView.findViewById(R.id.removeBtn);
            btnAddtoCart = (MaterialButton) itemView.findViewById(R.id.btnAddtoCart);

            menu_detail_st_cartbtn = itemView.findViewById(R.id.menu_detail_st_cartbtn);
            menu_detail_st_likebtn = itemView.findViewById(R.id.menu_detail_st_likebtn);

            mProgress = (ProgressBar) itemView.findViewById(R.id.product_detail_image_progress);
        }
    }

    protected class MovieVH extends RecyclerView.ViewHolder {
        private TextView mMovieTitle;
        private TextView mMovieDesc;
        private TextView mMoviePrice;
        private TextView mYear; // displays "year | language"
        private ImageView mPosterImg;
        private ProgressBar mProgress;
        private Button menu_st_likebtn, menu_st_carttn;

        public MovieVH(View itemView) {
            super(itemView);

            mMovieTitle = (TextView) itemView.findViewById(R.id.movie_title);
            mMovieDesc = (TextView) itemView.findViewById(R.id.movie_desc);
            mMoviePrice = (TextView) itemView.findViewById(R.id.movie_price);
            mYear = (TextView) itemView.findViewById(R.id.movie_year);
            mPosterImg = (ImageView) itemView.findViewById(R.id.movie_poster);
            mProgress = (ProgressBar) itemView.findViewById(R.id.movie_progress);

            menu_st_carttn = (Button) itemView.findViewById(R.id.menu_st_carttn);
            menu_st_likebtn = (Button)  itemView.findViewById(R.id.menu_st_likebtn);

        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private TextView mErrorTxt;
        private LinearLayout mErrorLayout;

        public LoadingVH(View itemView) {
            super(itemView);

            mProgressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = (ImageButton) itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = (TextView) itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = (LinearLayout) itemView.findViewById(R.id.loadmore_errorlayout);

            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:
                    showRetry(false, null);
                    mCallback.retryPageLoad();
                    break;
            }
        }
    }


}