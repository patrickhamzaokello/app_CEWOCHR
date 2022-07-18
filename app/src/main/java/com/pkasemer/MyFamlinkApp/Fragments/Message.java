package com.pkasemer.MyFamlinkApp.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pkasemer.MyFamlinkApp.Adapters.UserOrdersAdapter;
import com.pkasemer.MyFamlinkApp.Apis.ApiBase;
import com.pkasemer.MyFamlinkApp.Apis.ApiEndPoints;
import com.pkasemer.MyFamlinkApp.HelperClasses.SharedPrefManager;
import com.pkasemer.MyFamlinkApp.Models.UserFeedback;
import com.pkasemer.MyFamlinkApp.Models.UserMessage;
import com.pkasemer.MyFamlinkApp.Models.UserModel;
import com.pkasemer.MyFamlinkApp.R;
import com.pkasemer.MyFamlinkApp.RootActivity;
import com.pkasemer.MyFamlinkApp.Utils.PaginationScrollListener;

import java.util.List;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.pkasemer.MyFamlinkApp.Utils.PaginationAdapterCallback;

public class Message extends Fragment implements PaginationAdapterCallback{


    public Message() {
        // Required empty public constructor
    }

    UserOrdersAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    RecyclerView rv;
    ProgressBar progressBar;
    LinearLayout errorLayout;
    Button btnRetry;
    TextView txtError;
    SwipeRefreshLayout swipeRefreshLayout;

    private static final int PAGE_START = 1;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private static int TOTAL_PAGES = 5;
    private int currentPage = PAGE_START;
    private int customerId;

    private ApiEndPoints apiEndPoints;
    private Object PaginationAdapterCallback;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_messages, container, false);

        rv = view.findViewById(R.id.main_recycler);
        progressBar = view.findViewById(R.id.main_progress);
        errorLayout = view.findViewById(R.id.error_layout);
        btnRetry = view.findViewById(R.id.error_btn_retry);
        txtError = view.findViewById(R.id.error_txt_cause);
        swipeRefreshLayout = view.findViewById(R.id.main_swiperefresh);

        adapter = new UserOrdersAdapter(getContext(),  this);

        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());

        rv.setAdapter(adapter);

        rv.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                loadNextPage();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        //init service and load data
        apiEndPoints = ApiBase.getClient(getContext()).create(ApiEndPoints.class);
        btnRetry.setOnClickListener(v -> loadFirstPage());
        swipeRefreshLayout.setOnRefreshListener(this::doRefresh);


        UserModel user = SharedPrefManager.getInstance(getContext()).getUser();
        customerId = user.getId();
        loadFirstPage();

        return view;
    }




    /**
     * Triggers the actual background refresh via the {@link SwipeRefreshLayout}
     */
    private void doRefresh() {
        progressBar.setVisibility(View.VISIBLE);
        if (callUserOrdersApi().isExecuted())
            callUserOrdersApi().cancel();

        // TODO: Check if data is stale.
        //  Execute network request if cache is expired; otherwise do not update data.
        adapter.getMovies().clear();
        adapter.notifyDataSetChanged();
        loadFirstPage();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void loadFirstPage() {

        hideErrorView();
        currentPage = PAGE_START;

        callUserOrdersApi().enqueue(new Callback<UserFeedback>() {
            @Override
            public void onResponse(Call<UserFeedback> call, Response<UserFeedback> response) {
                hideErrorView();

                Log.i("onResponse: " , (response.raw().cacheResponse() != null ? "Cache" : "Network"));

                // Got data. Send it to adapter
                List<UserMessage> userOrdersResultList = fetchResults(response);
                Log.d("pks", String.valueOf(userOrdersResultList));
                progressBar.setVisibility(View.GONE);
                if(userOrdersResultList.isEmpty()){
                    showCategoryErrorView();
                    return;
                } else {
                    if(TOTAL_PAGES > 0){
                        adapter.addAll(userOrdersResultList);
                    } else {
                        showCategoryErrorView();
                    }
                }

                if (currentPage < TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<UserFeedback> call, Throwable t) {
                t.printStackTrace();
                showErrorView(t);
            }
        });
    }


    private List<UserMessage> fetchResults(Response<UserFeedback> response) {
        UserFeedback userOrders = response.body();
        TOTAL_PAGES = userOrders.getTotalPages();
        System.out.println("total pages-" + TOTAL_PAGES);
        return userOrders.getUserMessage();
    }

    private void loadNextPage() {

        callUserOrdersApi().enqueue(new Callback<UserFeedback>() {
            @Override
            public void onResponse(Call<UserFeedback> call, Response<UserFeedback> response) {


                adapter.removeLoadingFooter();
                isLoading = false;

                List<UserMessage> userOrdersResultList = fetchResults(response);
                adapter.addAll(userOrdersResultList);

                if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<UserFeedback> call, Throwable t) {
                t.printStackTrace();
                adapter.showRetry(true, fetchErrorMessage(t));
            }
        });
    }


    /**
     * Performs a Retrofit call to the top rated movies API.
     * Same API call for Pagination.
     * As {@link #currentPage} will be incremented automatically
     * by @{@link PaginationScrollListener} to load next page.
     */
    private Call<UserFeedback> callUserOrdersApi() {
        return apiEndPoints.getUserOrders(
                customerId,
                currentPage
        );
    }


    @Override
    public void retryPageLoad() {
        loadNextPage();
    }

    @Override
    public void requestfailed() {
        Toast.makeText(getContext(),"Message not found",Toast.LENGTH_SHORT).show();
    }


    /**
     * @param throwable required for {@link #fetchErrorMessage(Throwable)}
     * @return
     */
    private void showErrorView(Throwable throwable) {

        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            txtError.setText(fetchErrorMessage(throwable));
        }
    }

    private void showCategoryErrorView() {

        progressBar.setVisibility(View.GONE);

        AlertDialog.Builder android = new AlertDialog.Builder(getContext());
        android.setTitle("Famlink");
        android.setIcon(R.drawable.drw);
        android.setMessage("we can't find any feedback for you, try again")
                .setCancelable(false)

                .setPositiveButton("Home", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //go to activity
                        Intent intent = new Intent(getContext(), RootActivity.class);
                        startActivity(intent);
                    }
                });
        android.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //go to activity
                Intent intent = new Intent(getContext(), RootActivity.class);
                startActivity(intent);
            }
        });
        android.create().show();

    }



    /**
     * @param throwable to identify the type of error
     * @return appropriate error message
     */
    private String fetchErrorMessage(Throwable throwable) {
        String errorMsg = getResources().getString(R.string.error_msg_unknown);

        if (!isNetworkConnected()) {
            errorMsg = getResources().getString(R.string.error_msg_no_internet);
        } else if (throwable instanceof TimeoutException) {
            errorMsg = getResources().getString(R.string.error_msg_timeout);
        }

        return errorMsg;
    }

    // Helpers -------------------------------------------------------------------------------------


    private void hideErrorView() {
        if (errorLayout.getVisibility() == View.VISIBLE) {
            errorLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Remember to add android.permission.ACCESS_NETWORK_STATE permission.
     *
     * @return
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(getContext().CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }




}