package com.pkasemer.MyFamlinkApp.Adapters;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.pkasemer.MyFamlinkApp.HelperClasses.ReportsInterface;
import com.pkasemer.MyFamlinkApp.Models.Case;
import com.pkasemer.MyFamlinkApp.R;
import com.pkasemer.MyFamlinkApp.localDatabase.DatabaseHelper;

import java.util.List;

/**
 * Created by Suleiman on 19/10/16.
 */

public class ReportsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private static final String BASE_URL_IMG = "";

    private List<Case> caseModelList;
    DatabaseHelper db;


    private Context context;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;


    DrawableCrossFadeFactory factory =
            new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();

    private String errorMsg;



    public ReportsAdapter(Context context, List<Case> caseModelList) {
        this.context = context;
        this.caseModelList = caseModelList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
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
        View v1 = inflater.inflate(R.layout.case_design, parent, false);
        viewHolder = new CartDesignVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Case nameitem = caseModelList.get(position); // Food
        db = new DatabaseHelper(holder.itemView.getContext());


        switch (getItemViewType(position)) {
            case ITEM:
                final CartDesignVH movieVH = (CartDesignVH) holder;

                movieVH.cart_product_name.setText(nameitem.getName());

                movieVH.child_description.setText(nameitem.getDescription());
                movieVH.child_category.setText(nameitem.getCase_category());
                movieVH.case_location.setText("location: "+nameitem.getLocation());


                if (nameitem.getStatus() == 0)
                    movieVH.imageViewStatus.setBackgroundResource(R.drawable.ic_offline);
                else
                    movieVH.imageViewStatus.setBackgroundResource(R.drawable.ic_success);



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
        return caseModelList == null ? 0 : caseModelList.size();
    }

    @Override
    public int getItemViewType(int position) {

        return (position == caseModelList.size() - 1 && isLoadingAdded) ?
                LOADING : ITEM;
    }





    public void remove(Case r) {
        int position = caseModelList.indexOf(r);
        if (position > -1) {
            caseModelList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void updateFood(Case r) {
        int position = caseModelList.indexOf(r);
        if (position > -1) {
            db = new DatabaseHelper(context.getApplicationContext());
            caseModelList = db.list_DB_Cases();
            notifyDataSetChanged();
        }
    }


    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(caseModelList.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }


    protected class CartDesignVH extends RecyclerView.ViewHolder {
        private TextView cart_product_name, child_description,child_category,case_location;
        private ImageView imageViewStatus;

        public CartDesignVH(View itemView) {
            super(itemView);
            cart_product_name = itemView.findViewById(R.id.textViewName);
            child_description = itemView.findViewById(R.id.child_description);
            imageViewStatus = itemView.findViewById(R.id.imageViewStatus);
            child_category = itemView.findViewById(R.id.child_category);
            case_location = itemView.findViewById(R.id.case_location);
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
                    break;
            }
        }
    }


}