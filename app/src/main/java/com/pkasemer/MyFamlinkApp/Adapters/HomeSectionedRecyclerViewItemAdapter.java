package com.pkasemer.MyFamlinkApp.Adapters;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.pkasemer.MyFamlinkApp.Models.SectionedMenuItem;
import com.pkasemer.MyFamlinkApp.MyMenuDetail;
import com.pkasemer.MyFamlinkApp.R;
import com.pkasemer.MyFamlinkApp.RootActivity;
import com.pkasemer.MyFamlinkApp.Utils.GlideApp;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class HomeSectionedRecyclerViewItemAdapter extends RecyclerView.Adapter<HomeSectionedRecyclerViewItemAdapter.ItemViewHolder> {

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView itemLabel, itemprice, itemdesc;
        private ImageView itemimage;
        private ProgressBar mProgress;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemimage = (ImageView) itemView.findViewById(R.id.product_imageview);
            itemLabel = (TextView) itemView.findViewById(R.id.item_name);
            itemprice = (TextView) itemView.findViewById(R.id.item_label);
            itemdesc = (TextView) itemView.findViewById(R.id.item_label_desc);
            mProgress = (ProgressBar) itemView.findViewById(R.id.home_product_image_progress);


        }
    }

    private Context context;
    private List<SectionedMenuItem> sectionedMenuItems;
    //    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/w150";
    private static final String BASE_URL_IMG = "";

    DrawableCrossFadeFactory factory =
            new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();

    public HomeSectionedRecyclerViewItemAdapter(Context context, List<SectionedMenuItem> sectionedMenuItems) {
        this.context = context;
        this.sectionedMenuItems = sectionedMenuItems;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_section_item_custom_row_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        final SectionedMenuItem sectionedMenuItem = sectionedMenuItems.get(position);


        holder.itemdesc.setText(sectionedMenuItem.getDescription());
        holder.itemLabel.setText(sectionedMenuItem.getMenuName());
        holder.itemprice.setText("Ugx " + NumberFormat.getNumberInstance(Locale.US).format(sectionedMenuItem.getPrice()));

        Glide
                .with(context)
                .load(BASE_URL_IMG + sectionedMenuItem.getMenuImage())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.mProgress.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.mProgress.setVisibility(View.GONE);
                        return false;
                    }

                })
                .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
                .centerCrop()
                .transition(withCrossFade(factory))
                .into(holder.itemimage);


        //show toast on click of show all button
        holder.itemimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context.getApplicationContext(), MyMenuDetail.class);
                //PACK DATA
                i.putExtra("SENDER_KEY", "MenuDetails");
                i.putExtra("selectMenuId", sectionedMenuItem.getMenuId());
                i.putExtra("category_selected_key", sectionedMenuItem.getMenuTypeId());
                context.startActivity(i);
            }
        });
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

    private RequestBuilder< Drawable > loadImage(@NonNull String posterPath) {
        return GlideApp
                .with(context)
                .load(BASE_URL_IMG + posterPath)
                .centerCrop();
    }

    @Override
    public int getItemCount() {
        return sectionedMenuItems.size();
    }


}