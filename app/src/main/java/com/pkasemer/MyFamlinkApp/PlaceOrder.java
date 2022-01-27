package com.pkasemer.MyFamlinkApp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.pkasemer.MyFamlinkApp.Apis.MovieApi;
import com.pkasemer.MyFamlinkApp.Apis.MovieService;
import com.pkasemer.MyFamlinkApp.Models.FoodDBModel;

import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceOrder extends AppCompatActivity {

    private MovieService movieService;
    FoodDBModel foodDBModel;
    
    Button btnCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        getSupportActionBar().setTitle("Zodongo Foods"); // set the top title
        String title = actionBar.getTitle().toString(); // get the title
        actionBar.hide();

        btnCheckout = findViewById(R.id.btnCheckout);

        //init service and load data
        movieService = MovieApi.getClient(PlaceOrder.this).create(MovieService.class);
        
        
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFirstPage();
            }
        });
    }
    
    

    
    private void loadFirstPage() {

        // To ensure list is visible when retry button in error view is clicked
        hideErrorView();

        placeUserOrder().enqueue(new Callback<FoodDBModel>() {
            @Override
            public void onResponse(Call<FoodDBModel> call, Response<FoodDBModel> response) {
                hideErrorView();

                Log.i("create", "onResponse: " + (response.raw().cacheResponse() != null ? "Cache" : "Network"));

              
                
            }

            @Override
            public void onFailure(Call<FoodDBModel> call, Throwable t) {
                t.printStackTrace();
                showErrorView(t);
            }
        });
    }



    private Call<FoodDBModel> placeUserOrder(){
        return  movieService.postCartItems(
                foodDBModel
        );
    }

    /**
     * @param throwable required for {@link #fetchErrorMessage(Throwable)}
     * @return
     */
    private void showErrorView(Throwable throwable) {

//        if (errorLayout.getVisibility() == View.GONE) {
//            errorLayout.setVisibility(View.VISIBLE);
//            progressBar.setVisibility(View.GONE);
//
//            txtError.setText(fetchErrorMessage(throwable));
//        }
    }

    private void showCategoryErrorView() {

//        progressBar.setVisibility(View.GONE);

        AlertDialog.Builder android = new AlertDialog.Builder(PlaceOrder.this);
        android.setTitle("Coming Soon");
        android.setIcon(R.drawable.africanwoman);
        android.setMessage("This Menu Category will be updated with great tastes soon, Stay Alert for Updates.")
                .setCancelable(false)

                .setPositiveButton("Home", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //go to activity
                        Intent intent = new Intent(PlaceOrder.this, RootActivity.class);
                        startActivity(intent);
                    }
                });
        android.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //go to activity
                Intent intent = new Intent(PlaceOrder.this, RootActivity.class);
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
//        if (errorLayout.getVisibility() == View.VISIBLE) {
//            errorLayout.setVisibility(View.GONE);
//            progressBar.setVisibility(View.VISIBLE);
//        }
    }

    /**
     * Remember to add android.permission.ACCESS_NETWORK_STATE permission.
     *
     * @return
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) PlaceOrder.this.getSystemService(PlaceOrder.this.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

}