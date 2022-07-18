package com.pkasemer.MyFamlinkApp.Utils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pkasemer.MyFamlinkApp.Apis.ApiBase;
import com.pkasemer.MyFamlinkApp.Apis.ApiEndPoints;
import com.pkasemer.MyFamlinkApp.HelperClasses.SharedPrefManager;
import com.pkasemer.MyFamlinkApp.Models.PostResponse;
import com.pkasemer.MyFamlinkApp.Models.Referal;
import com.pkasemer.MyFamlinkApp.Models.UserModel;
import com.pkasemer.MyFamlinkApp.ReportChild;
import com.pkasemer.MyFamlinkApp.RootActivity;
import com.pkasemer.MyFamlinkApp.Singletons.VolleySingleton;
import com.pkasemer.MyFamlinkApp.localDatabase.DatabaseHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import static com.pkasemer.MyFamlinkApp.HttpRequests.URLs.URL_SAVE_NAME;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;


public class NetworkStateChecker extends BroadcastReceiver {

    //context and database helper object
    private Context context;
    private DatabaseHelper db;
    private ApiEndPoints apiEndPoints;
    Referal referal = new Referal();
    @SuppressLint("Range")
    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        db = new DatabaseHelper(context);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //initializing views and objects
        apiEndPoints = ApiBase.getClient(context).create(ApiEndPoints.class);

        //if there is a network
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

                //getting all the unsynced names
                Cursor cursor = db.getUnsyncedNames();
                if (cursor.moveToFirst()) {
                    do {
                        //calling the method to save the unsynced name to MySQL
                        saveNameToServer(
                                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_LOCATION)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CASE_CATEGORY)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE))

                                );
                    } while (cursor.moveToNext());
                }
            }
        }
    }

    /*
     * method taking two arguments
     * name that is to be saved and id of the name from SQLite
     * if the name is successfully sent
     * we will update the status as synced in SQLite
     * */


    private Call<PostResponse> createReferralCase() {
        return apiEndPoints.postReferCase(referal);
    }


    /*
     * this method is saving the name to the server
     * */
    private void saveNameToServer(final int id, String description, final String address, final String category_id, final String reportedby_id, final String title) {

        UserModel user = SharedPrefManager.getInstance(context).getUser();

        referal.setTitle(title);
        referal.setDescription(description);
        referal.setAddress(address);
        referal.setCategoryId(category_id);
        referal.setLongitude(2.239878798827563);
        referal.setLatitude(32.89395403994614);
        referal.setPicture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRNsJyFJ1hSBVJ4mVkdeyNNJCTR3QyYaEHjug&amp;usqp=CAU");
        referal.setReportedbyId(String.valueOf(user.getId()));

        createReferralCase().enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, retrofit2.Response<PostResponse> response) {

                //set response body to match OrderResponse Model
                PostResponse postResponse = response.body();

                //if orderResponses is not null
                if (postResponse != null) {
                    //if no error- that is error = false
                    if (!postResponse.getError()) {
                        Log.i("Case Success", postResponse.getMessage() + postResponse.getError());
                        //if there is a success
                        //storing the name to sqlite with status synced

                        //updating the status in sqlite
                        db.updateNameStatus(id, ReportChild.NAME_SYNCED_WITH_SERVER);

                        //sending the broadcast to refresh the list
                        context.sendBroadcast(new Intent(ReportChild.DATA_SAVED_BROADCAST));
                    } else {
                        Log.i("Ress", "message: " + (postResponse.getMessage()));
                        Log.i("et", "error false: " + (postResponse.getError()));


                    }


                } else {
                    Log.i("Referral Response null", "Order is null Try Again: ");

                    return;

                }

            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                t.printStackTrace();
                Log.i("Case Failed", "Case Failed Try Again: " + t);
            }
        });

    }

}
