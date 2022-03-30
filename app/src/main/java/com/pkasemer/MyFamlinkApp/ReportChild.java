package com.pkasemer.MyFamlinkApp;

import static com.pkasemer.MyFamlinkApp.HttpRequests.URLs.URL_SAVE_NAME;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.pkasemer.MyFamlinkApp.Adapters.NameAdapter;
import com.pkasemer.MyFamlinkApp.Apis.MovieApi;
import com.pkasemer.MyFamlinkApp.Apis.MovieService;
import com.pkasemer.MyFamlinkApp.HelperClasses.SharedPrefManager;
import com.pkasemer.MyFamlinkApp.Models.Name;
import com.pkasemer.MyFamlinkApp.Models.PostResponse;
import com.pkasemer.MyFamlinkApp.Models.Referal;
import com.pkasemer.MyFamlinkApp.Models.UserModel;
import com.pkasemer.MyFamlinkApp.Singletons.VolleySingleton;
import com.pkasemer.MyFamlinkApp.Utils.NetworkStateChecker;
import com.pkasemer.MyFamlinkApp.localDatabase.DatabaseHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;

public class ReportChild extends AppCompatActivity {


    //database helper object
    private DatabaseHelper db;

    //View objects
    private Button buttonSave;
    private EditText editTextName;
    private EditText editTextDescription;
    private EditText editTextLocation;

    //List to store all the names

    //1 means data is synced and 0 means data is not synced
    public static final int NAME_SYNCED_WITH_SERVER = 1;
    public static final int NAME_NOT_SYNCED_WITH_SERVER = 0;

    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "net.simplifiedcoding.datasaved";

    //Broadcast receiver to know the sync status

    ActionBar actionBar;
    private MovieService movieService;

    Referal referal = new Referal();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_child);

        actionBar = getSupportActionBar(); // or getActionBar();
        actionBar.setTitle("Report Child");
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        //initializing views and objects
        movieService = MovieApi.getClient(ReportChild.this).create(MovieService.class);

        db = new DatabaseHelper(this);

        buttonSave = (Button) findViewById(R.id.buttonSave);
        editTextName = (TextInputEditText) findViewById(R.id.editTextName);
        editTextDescription = (TextInputEditText) findViewById(R.id.editTextDescription);
        editTextLocation = (TextInputEditText) findViewById(R.id.editTextLocation);


        //adding click listener to button
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNameToServer();
            }
        });


    }


    private Call<PostResponse> createReferralCase() {

        return movieService.postReferCase(referal);
    }


    /*
     * this method is saving the name to the server
     * */
    private void saveNameToServer() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving Name...");
        progressDialog.show();

        final String name = editTextName.getText().toString().trim();
        final String description = editTextDescription.getText().toString().trim();

        UserModel user = SharedPrefManager.getInstance(ReportChild.this).getUser();

        referal.setName(name);
        referal.setPicture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRNsJyFJ1hSBVJ4mVkdeyNNJCTR3QyYaEHjug&amp;usqp=CAU");
        referal.setDescription(description);
        referal.setLongitude(2.239878798827563);
        referal.setLatitude(32.89395403994614);
        referal.setReportedbyId(String.valueOf(user.getId()));

        createReferralCase().enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, retrofit2.Response<PostResponse> response) {

                //set response body to match OrderResponse Model
                PostResponse postResponse = response.body();
                progressDialog.dismiss();

                //if orderResponses is not null
                if (postResponse != null) {

                    //if no error- that is error = false
                    if (!postResponse.getError()) {
                        Log.i("Case Success", postResponse.getMessage() + postResponse.getError());
                        //if there is a success
                        //storing the name to sqlite with status synced
                        saveNameToLocalStorage(name, description, NAME_SYNCED_WITH_SERVER);
                        new SweetAlertDialog(ReportChild.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Case Referred")
                                .setContentText("This case has been referred successfully")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {

                                        sDialog.dismissWithAnimation();

                                        Intent i = new Intent(ReportChild.this, RootActivity.class);
                                        startActivity(i);
                                    }
                                }).show();

                    } else {
                        Log.i("Ress", "message: " + (postResponse.getMessage()));
                        Log.i("et", "error false: " + (postResponse.getError()));
//                        ShowOrderFailed();
                        new SweetAlertDialog(ReportChild.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText("Try again")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {

                                        sDialog.dismissWithAnimation();

                                        Intent i = new Intent(ReportChild.this, RootActivity.class);
                                        startActivity(i);
                                    }
                                }).show();

                    }


                } else {
                    Log.i("Referral Response null", "Order is null Try Again: ");

                    //if there is some error
                    //saving the name to sqlite with status unsynced
                    saveNameToLocalStorage(name, description, NAME_NOT_SYNCED_WITH_SERVER);

                    new SweetAlertDialog(ReportChild.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Case Saved Locally")
                            .setContentText("Information is saved on the phone")
                            .setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {

                                    sDialog.dismissWithAnimation();

                                    Intent i = new Intent(ReportChild.this, RootActivity.class);
                                    startActivity(i);
                                }
                            }).show();
                    return;

                }

            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                progressDialog.dismiss();

                t.printStackTrace();
                Log.i("Order Failed", "Order Failed Try Again: " + t);
                //on error storing the name to sqlite with status unsynced
                saveNameToLocalStorage(name, description, NAME_NOT_SYNCED_WITH_SERVER);

                new SweetAlertDialog(ReportChild.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Case Saved Locally")
                        .setContentText("Information is saved on the phone")
                        .setConfirmText("OK")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.dismissWithAnimation();

                                Intent i = new Intent(ReportChild.this, RootActivity.class);
                                startActivity(i);
                            }
                        }).show();
            }
        });

    }

    //saving the name to local storage
    private void saveNameToLocalStorage(String name, String description, int status) {
        db.addName(name, description, status);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.support_radio:
                if (checked)
                    // male clicked
                    referal.setCategoryId("support");
                break;
            case R.id.child_tracing_radio:
                if (checked)
                    referal.setCategoryId("child_tracing");
                break;

            case R.id.child_reunion_radio:
                if (checked)
                    referal.setCategoryId("child_reunion");
                break;
            case R.id.other_radio:
                if (checked)
                    referal.setCategoryId("other");

                break;
        }
    }
}