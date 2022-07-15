package com.pkasemer.MyFamlinkApp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;
import com.pkasemer.MyFamlinkApp.Apis.ApiBase;
import com.pkasemer.MyFamlinkApp.Apis.ApiEndPoints;
import com.pkasemer.MyFamlinkApp.HelperClasses.SharedPrefManager;
import com.pkasemer.MyFamlinkApp.Models.PostResponse;
import com.pkasemer.MyFamlinkApp.Models.Referal;
import com.pkasemer.MyFamlinkApp.Models.UserModel;
import com.pkasemer.MyFamlinkApp.localDatabase.DatabaseHelper;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;

public class ReportChild extends AppCompatActivity {


    //database helper object
    private DatabaseHelper db;

    //View objects
    private Button buttonSave;
    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextLocation;


    //1 means data is synced and 0 means data is not synced
    public static final int NAME_SYNCED_WITH_SERVER = 1;
    public static final int NAME_NOT_SYNCED_WITH_SERVER = 0;

    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "net.simplifiedcoding.datasaved";

    //Broadcast receiver to know the sync status

    ActionBar actionBar;
    private ApiEndPoints apiEndPoints;

    Referal referal = new Referal();
    String[] referal_types = {"Support", "Tracking","Child Reunion","Other"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;
    String choice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_child);

        actionBar = getSupportActionBar(); // or getActionBar();
        actionBar.setTitle("Report Case");
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        //initializing views and objects
        apiEndPoints = ApiBase.getClient(ReportChild.this).create(ApiEndPoints.class);

        db = new DatabaseHelper(this);
        autoCompleteTextView = findViewById(R.id.auto_complete_referral_txt);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                choice = parent.getItemAtPosition(position).toString();
            }
        });

        adapterItems = new ArrayAdapter<String>(getApplicationContext(), R.layout.child_referral_dropdown, referal_types);
        autoCompleteTextView.setAdapter(adapterItems);

        buttonSave = (Button) findViewById(R.id.buttonSave);
        editTextTitle = (TextInputEditText) findViewById(R.id.editTextTitle);
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
        return apiEndPoints.postReferCase(referal);
    }


    /*
     * this method is saving the name to the server
     * */
    private void saveNameToServer() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving Case...");
        progressDialog.show();

        final String title = editTextTitle.getText().toString().trim();
        final String description = editTextDescription.getText().toString().trim();
        final String address = editTextLocation.getText().toString().trim();
        UserModel user = SharedPrefManager.getInstance(ReportChild.this).getUser();

        referal.setTitle(title);
        referal.setDescription(description);
        referal.setAddress(address);
        referal.setCategoryId(choice);
        referal.setLongitude(2.239878798827563);
        referal.setLatitude(32.89395403994614);
        referal.setPicture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRNsJyFJ1hSBVJ4mVkdeyNNJCTR3QyYaEHjug&amp;usqp=CAU");
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
                        saveNameToLocalStorage(title, description, NAME_SYNCED_WITH_SERVER);
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
                    saveNameToLocalStorage(title, description, NAME_NOT_SYNCED_WITH_SERVER);

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
                saveNameToLocalStorage(title, description, NAME_NOT_SYNCED_WITH_SERVER);

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



}