package com.pkasemer.MyFamlinkApp.Fragments;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.google.android.material.button.MaterialButton;
import com.pkasemer.MyFamlinkApp.Models.SectionedMenuItem;
import com.pkasemer.MyFamlinkApp.Models.SelectedCategoryMenuItemResult;
import com.pkasemer.MyFamlinkApp.R;
import com.pkasemer.MyFamlinkApp.localDatabase.SenseDBHelper;

import java.text.NumberFormat;
import java.util.Locale;


public class MenuDetail extends Fragment {

    SectionedMenuItem sectionedMenuItem;
    SelectedCategoryMenuItemResult selectedCategoryMenuItemResult;
    ProgressBar product_detail_image_progress;
    ImageView menu_image;
    TextView menu_name, menu_shortinfo, menu_description, ratingnumber, menu_price, menu_qtn, itemQuanEt, menu_total_price;
    Button incrementQtn, decreaseQtn, btnAddtoCart, menu_detail_st_cartbtn,menu_detail_st_likebtn;


    private static final String BASE_URL_IMG = "";
    int minteger = 1;
    int totalPrice;
    private int modeltype;

    SenseDBHelper db;
    boolean food_db_itemchecker;

    DrawableCrossFadeFactory factory =
            new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();

    public MenuDetail() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null && bundle.containsKey("modeltype")) {
            modeltype = Integer.parseInt(bundle.getString("modeltype"));
            db = new SenseDBHelper(getContext());

            if (modeltype == 0) {
                sectionedMenuItem = bundle.getParcelable("sectionedMenuItem"); // Key

            } else {
                selectedCategoryMenuItemResult = bundle.getParcelable("sectionedMenuItem");
            }

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu_detail, container, false);

        product_detail_image_progress = (ProgressBar) view.findViewById(R.id.product_detail_image_progress);
        menu_image = (ImageView) view.findViewById(R.id.menu_image);
        menu_name = (TextView) view.findViewById(R.id.menu_name);
        menu_shortinfo = (TextView) view.findViewById(R.id.menu_shortinfo);
        menu_description = (TextView) view.findViewById(R.id.menu_description);
        ratingnumber = (TextView) view.findViewById(R.id.ratingnumber);
        menu_price = (TextView) view.findViewById(R.id.menu_price);
        menu_qtn = (TextView) view.findViewById(R.id.menu_qtn);
        itemQuanEt = (TextView) view.findViewById(R.id.itemQuanEt);
        menu_total_price = (TextView) view.findViewById(R.id.menu_total_price);
        incrementQtn = view.findViewById(R.id.addBtn);
        decreaseQtn = view.findViewById(R.id.removeBtn);
        btnAddtoCart = (MaterialButton) view.findViewById(R.id.btnAddtoCart);

        menu_detail_st_cartbtn = view.findViewById(R.id.menu_detail_st_cartbtn);
        menu_detail_st_likebtn = view.findViewById(R.id.menu_detail_st_likebtn);



        Glide
                .with(getContext())
                .load(BASE_URL_IMG + (modeltype == 0 ? sectionedMenuItem.getMenuImage() : selectedCategoryMenuItemResult.getMenuImage()))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        product_detail_image_progress.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        product_detail_image_progress.setVisibility(View.GONE);
                        return false;
                    }

                })
                .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
                .centerCrop()
                .transition(withCrossFade(factory))
                .into(menu_image);


        menu_name.setText((modeltype == 0 ? sectionedMenuItem.getMenuName() : selectedCategoryMenuItemResult.getMenuName()));
        menu_shortinfo.setText("Duration: 30 Mins");
        menu_description.setText((modeltype == 0 ? sectionedMenuItem.getDescription() : selectedCategoryMenuItemResult.getDescription()));
        ratingnumber.setText((modeltype == 0 ? sectionedMenuItem.getRating() : selectedCategoryMenuItemResult.getRating()) + " | " + "5");
        menu_price.setText(NumberFormat.getNumberInstance(Locale.US).format((modeltype == 0 ? sectionedMenuItem.getPrice() : selectedCategoryMenuItemResult.getPrice())));
        menu_qtn.setText("1");
        menu_total_price.setText(NumberFormat.getNumberInstance(Locale.US).format((modeltype == 0 ? sectionedMenuItem.getPrice() : selectedCategoryMenuItemResult.getPrice())));

        food_db_itemchecker = db.checktweetindb(String.valueOf(modeltype == 0 ? sectionedMenuItem.getMenuId() : selectedCategoryMenuItemResult.getMenuId()));

        updatecartCount();

        if (food_db_itemchecker) {
            btnAddtoCart.setText("Add to Cart");
            btnAddtoCart.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.purple_200));
            btnAddtoCart.setTextColor(ContextCompat.getColor(view.getContext(), R.color.white));
            itemQuanEt.setText("1");

            menu_detail_st_cartbtn.setBackground(getResources().getDrawable(R.drawable.custom_cart_btn));
            menu_detail_st_likebtn.setBackground(getResources().getDrawable(R.drawable.custom_cart_like_btn));




        } else {
            btnAddtoCart.setText("Remove from Cart");
            btnAddtoCart.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.purple_200));
            btnAddtoCart.setTextColor(ContextCompat.getColor(view.getContext(), R.color.white));

            minteger = db.getMenuQtn(String.valueOf(modeltype == 0 ? sectionedMenuItem.getMenuId() : selectedCategoryMenuItemResult.getMenuId()));

            display(minteger);

            menu_detail_st_cartbtn.setBackground(getResources().getDrawable(R.drawable.custom_cart_btn_done));
            menu_detail_st_likebtn.setBackground(getResources().getDrawable(R.drawable.custom_cart_like_btn_done));


        }

        incrementQtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minteger = minteger + 1;
                display(minteger);
            }
        });

        decreaseQtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minteger = minteger - 1;

                if (minteger <= 1) {
                    minteger = 1;
                    display(minteger);
                } else {
                    display(minteger);
                }
            }
        });

        btnAddtoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                food_db_itemchecker = db.checktweetindb(String.valueOf(modeltype == 0 ? sectionedMenuItem.getMenuId() : selectedCategoryMenuItemResult.getMenuId()));


                if (food_db_itemchecker) {
                    db.addTweet(
                            modeltype == 0 ? sectionedMenuItem.getMenuId() : selectedCategoryMenuItemResult.getMenuId(),
                            modeltype == 0 ? sectionedMenuItem.getMenuName() : selectedCategoryMenuItemResult.getMenuName(),
                            modeltype == 0 ? sectionedMenuItem.getPrice() : selectedCategoryMenuItemResult.getPrice(),
                            modeltype == 0 ? sectionedMenuItem.getDescription() : selectedCategoryMenuItemResult.getDescription(),
                            modeltype == 0 ? sectionedMenuItem.getMenuTypeId() : selectedCategoryMenuItemResult.getMenuTypeId(),
                            modeltype == 0 ? sectionedMenuItem.getMenuImage() : selectedCategoryMenuItemResult.getMenuImage(),
                            modeltype == 0 ? sectionedMenuItem.getBackgroundImage() : selectedCategoryMenuItemResult.getBackgroundImage(),
                            modeltype == 0 ? sectionedMenuItem.getIngredients() : selectedCategoryMenuItemResult.getIngredients(),
                            modeltype == 0 ? sectionedMenuItem.getMenuStatus() : selectedCategoryMenuItemResult.getMenuStatus(),
                            modeltype == 0 ? sectionedMenuItem.getCreated() : selectedCategoryMenuItemResult.getCreated(),
                            modeltype == 0 ? sectionedMenuItem.getModified() : selectedCategoryMenuItemResult.getModified(),
                            modeltype == 0 ? sectionedMenuItem.getRating() : selectedCategoryMenuItemResult.getRating(),
                            minteger
                    );




                    btnAddtoCart.setText("Remove from Cart");
                    btnAddtoCart.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.purple_200));
                    btnAddtoCart.setTextColor(ContextCompat.getColor(v.getContext(), R.color.white));

                    menu_detail_st_cartbtn.setBackground(getResources().getDrawable(R.drawable.custom_cart_btn_done));
                    menu_detail_st_likebtn.setBackground(getResources().getDrawable(R.drawable.custom_cart_like_btn_done));

                    updatecartCount();


                } else {
                    db.deleteTweet(String.valueOf(modeltype == 0 ? sectionedMenuItem.getMenuId() : selectedCategoryMenuItemResult.getMenuId()));

                    btnAddtoCart.setText("Add to Cart");
                    btnAddtoCart.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.purple_200));
                    btnAddtoCart.setTextColor(ContextCompat.getColor(view.getContext(), R.color.white));


                    menu_detail_st_cartbtn.setBackground(getResources().getDrawable(R.drawable.custom_cart_btn));
                    menu_detail_st_likebtn.setBackground(getResources().getDrawable(R.drawable.custom_cart_like_btn));


                    updatecartCount();

                }
            }
        });


        menu_detail_st_cartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                food_db_itemchecker = db.checktweetindb(String.valueOf(modeltype == 0 ? sectionedMenuItem.getMenuId() : selectedCategoryMenuItemResult.getMenuId()));


                if (food_db_itemchecker) {
                    db.addTweet(
                            modeltype == 0 ? sectionedMenuItem.getMenuId() : selectedCategoryMenuItemResult.getMenuId(),
                            modeltype == 0 ? sectionedMenuItem.getMenuName() : selectedCategoryMenuItemResult.getMenuName(),
                            modeltype == 0 ? sectionedMenuItem.getPrice() : selectedCategoryMenuItemResult.getPrice(),
                            modeltype == 0 ? sectionedMenuItem.getDescription() : selectedCategoryMenuItemResult.getDescription(),
                            modeltype == 0 ? sectionedMenuItem.getMenuTypeId() : selectedCategoryMenuItemResult.getMenuTypeId(),
                            modeltype == 0 ? sectionedMenuItem.getMenuImage() : selectedCategoryMenuItemResult.getMenuImage(),
                            modeltype == 0 ? sectionedMenuItem.getBackgroundImage() : selectedCategoryMenuItemResult.getBackgroundImage(),
                            modeltype == 0 ? sectionedMenuItem.getIngredients() : selectedCategoryMenuItemResult.getIngredients(),
                            modeltype == 0 ? sectionedMenuItem.getMenuStatus() : selectedCategoryMenuItemResult.getMenuStatus(),
                            modeltype == 0 ? sectionedMenuItem.getCreated() : selectedCategoryMenuItemResult.getCreated(),
                            modeltype == 0 ? sectionedMenuItem.getModified() : selectedCategoryMenuItemResult.getModified(),
                            modeltype == 0 ? sectionedMenuItem.getRating() : selectedCategoryMenuItemResult.getRating(),
                            minteger
                    );




                    btnAddtoCart.setText("Remove from Cart");
                    btnAddtoCart.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.purple_200));
                    btnAddtoCart.setTextColor(ContextCompat.getColor(v.getContext(), R.color.white));

                    menu_detail_st_cartbtn.setBackground(getResources().getDrawable(R.drawable.custom_cart_btn_done));
                    menu_detail_st_likebtn.setBackground(getResources().getDrawable(R.drawable.custom_cart_like_btn_done));

                    updatecartCount();


                } else {
                    db.deleteTweet(String.valueOf(modeltype == 0 ? sectionedMenuItem.getMenuId() : selectedCategoryMenuItemResult.getMenuId()));

                    btnAddtoCart.setText("Add to Cart");
                    btnAddtoCart.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.purple_200));
                    btnAddtoCart.setTextColor(ContextCompat.getColor(view.getContext(), R.color.white));


                    menu_detail_st_cartbtn.setBackground(getResources().getDrawable(R.drawable.custom_cart_btn));
                    menu_detail_st_likebtn.setBackground(getResources().getDrawable(R.drawable.custom_cart_like_btn));


                    updatecartCount();

                }
            }
        });


        menu_detail_st_likebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                food_db_itemchecker = db.checktweetindb(String.valueOf(modeltype == 0 ? sectionedMenuItem.getMenuId() : selectedCategoryMenuItemResult.getMenuId()));


                if (food_db_itemchecker) {
                    db.addTweet(
                            modeltype == 0 ? sectionedMenuItem.getMenuId() : selectedCategoryMenuItemResult.getMenuId(),
                            modeltype == 0 ? sectionedMenuItem.getMenuName() : selectedCategoryMenuItemResult.getMenuName(),
                            modeltype == 0 ? sectionedMenuItem.getPrice() : selectedCategoryMenuItemResult.getPrice(),
                            modeltype == 0 ? sectionedMenuItem.getDescription() : selectedCategoryMenuItemResult.getDescription(),
                            modeltype == 0 ? sectionedMenuItem.getMenuTypeId() : selectedCategoryMenuItemResult.getMenuTypeId(),
                            modeltype == 0 ? sectionedMenuItem.getMenuImage() : selectedCategoryMenuItemResult.getMenuImage(),
                            modeltype == 0 ? sectionedMenuItem.getBackgroundImage() : selectedCategoryMenuItemResult.getBackgroundImage(),
                            modeltype == 0 ? sectionedMenuItem.getIngredients() : selectedCategoryMenuItemResult.getIngredients(),
                            modeltype == 0 ? sectionedMenuItem.getMenuStatus() : selectedCategoryMenuItemResult.getMenuStatus(),
                            modeltype == 0 ? sectionedMenuItem.getCreated() : selectedCategoryMenuItemResult.getCreated(),
                            modeltype == 0 ? sectionedMenuItem.getModified() : selectedCategoryMenuItemResult.getModified(),
                            modeltype == 0 ? sectionedMenuItem.getRating() : selectedCategoryMenuItemResult.getRating(),
                            minteger
                    );




                    btnAddtoCart.setText("Remove from Cart");
                    btnAddtoCart.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.purple_200));
                    btnAddtoCart.setTextColor(ContextCompat.getColor(v.getContext(), R.color.white));

                    menu_detail_st_cartbtn.setBackground(getResources().getDrawable(R.drawable.custom_cart_btn_done));
                    menu_detail_st_likebtn.setBackground(getResources().getDrawable(R.drawable.custom_cart_like_btn_done));

                    updatecartCount();


                } else {
                    db.deleteTweet(String.valueOf(modeltype == 0 ? sectionedMenuItem.getMenuId() : selectedCategoryMenuItemResult.getMenuId()));
                    btnAddtoCart.setText("Add to Cart");
                    btnAddtoCart.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.purple_200));
                    btnAddtoCart.setTextColor(ContextCompat.getColor(view.getContext(), R.color.white));


                    menu_detail_st_cartbtn.setBackground(getResources().getDrawable(R.drawable.custom_cart_btn));
                    menu_detail_st_likebtn.setBackground(getResources().getDrawable(R.drawable.custom_cart_like_btn));


                    updatecartCount();

                }
            }
        });




        return view;
    }


    private void updatecartCount() {
        String mycartcount = String.valueOf(db.countCart());
        Intent intent = new Intent(getString(R.string.cartcoutAction));
        intent.putExtra(getString(R.string.cartCount), mycartcount);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }


    private void display(int number) {

        totalPrice = number * (modeltype == 0 ? sectionedMenuItem.getPrice() : selectedCategoryMenuItemResult.getPrice());

        itemQuanEt.setText("" + number);
        menu_total_price.setText(NumberFormat.getNumberInstance(Locale.US).format(totalPrice));

        food_db_itemchecker = db.checktweetindb(String.valueOf(modeltype == 0 ? sectionedMenuItem.getMenuId() : selectedCategoryMenuItemResult.getMenuId()));

        if (food_db_itemchecker) {
            return;
            //item doesnt exist
        } else {
            db.updateMenuCount(number, modeltype == 0 ? sectionedMenuItem.getMenuId() : selectedCategoryMenuItemResult.getMenuId() );
            //item exists

        }
    }

}