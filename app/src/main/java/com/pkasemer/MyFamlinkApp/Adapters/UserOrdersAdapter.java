package com.pkasemer.MyFamlinkApp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.pkasemer.MyFamlinkApp.Models.UserMessage;
import com.pkasemer.MyFamlinkApp.R;
import com.pkasemer.MyFamlinkApp.Utils.PaginationAdapterCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Suleiman on 19/10/16.
 */

public class UserOrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private static final int HERO = 2;

    //    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/w150";
    private static final String BASE_URL_IMG = "";


    private List<UserMessage> userOrdersResultList;
    private final Context context;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;


    DrawableCrossFadeFactory factory =
            new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();

    private final PaginationAdapterCallback mCallback;
    private String errorMsg;

    public UserOrdersAdapter(Context context, PaginationAdapterCallback callback) {
        this.context = context;
        this.mCallback = callback;
        userOrdersResultList = new ArrayList<>();
    }

    public List<UserMessage> getMovies() {
        return userOrdersResultList;
    }

    public void setMovies(List<UserMessage> userOrdersResultList) {
        this.userOrdersResultList = userOrdersResultList;
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
        View v1 = inflater.inflate(R.layout.user_message_design, parent, false);
        viewHolder = new MovieVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        UserMessage userOrdersResult = userOrdersResultList.get(position);// Movie

        switch (getItemViewType(position)) {
            case ITEM:
                final MovieVH movieVH = (MovieVH) holder;

                if (userOrdersResult.getId() != 0) {

                    movieVH.message.setText(userOrdersResult.getMessage());
                    movieVH.date.setText(userOrdersResult.getCreatedDate());


                } else {
                    movieVH.order_layout_card.setVisibility(View.GONE);
                    mCallback.requestfailed();
                }

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
        return userOrdersResultList == null ? 0 : userOrdersResultList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == userOrdersResultList.size() - 1 && isLoadingAdded) ?
                LOADING : ITEM;
    }







    /*
   Helpers
   _________________________________________________________________________________________________
    */



    public void add(UserMessage r) {
        userOrdersResultList.add(r);
        notifyItemInserted(userOrdersResultList.size() - 1);
    }

    public void addAll(List<UserMessage> userOrdersResultList1) {
        for (UserMessage userOrdersResult : userOrdersResultList1) {
            add(userOrdersResult);
        }
    }

    public void remove(UserMessage r) {
        int position = userOrdersResultList.indexOf(r);
        if (position > -1) {
            userOrdersResultList.remove(position);
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
        add(new UserMessage());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = userOrdersResultList.size() - 1;
        UserMessage userOrdersResult = getItem(position);

        if (userOrdersResult != null) {
            userOrdersResultList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(userOrdersResultList.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }

    public UserMessage getItem(int position) {
        return userOrdersResultList.get(position);
    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */

    protected class MovieVH extends RecyclerView.ViewHolder {
        private final TextView date, message;
        private final RelativeLayout order_layout_card;

        public MovieVH(View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.message);
            date = itemView.findViewById(R.id.date);
            order_layout_card = itemView.findViewById(R.id.order_layout_card);


        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ProgressBar mProgressBar;
        private final ImageButton mRetryBtn;
        private final TextView mErrorTxt;
        private final LinearLayout mErrorLayout;

        public LoadingVH(View itemView) {
            super(itemView);

            mProgressBar = itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = itemView.findViewById(R.id.loadmore_errorlayout);

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