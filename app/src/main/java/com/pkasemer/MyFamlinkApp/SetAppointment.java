package com.pkasemer.MyFamlinkApp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.pkasemer.MyFamlinkApp.Apis.MovieApi;
import com.pkasemer.MyFamlinkApp.Apis.MovieService;
import com.pkasemer.MyFamlinkApp.HelperClasses.SharedPrefManager;
import com.pkasemer.MyFamlinkApp.Models.Appointment;
import com.pkasemer.MyFamlinkApp.Models.PostResponse;
import com.pkasemer.MyFamlinkApp.Models.Referal;
import com.pkasemer.MyFamlinkApp.Models.UserModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;

public class SetAppointment extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    ActionBar actionBar;
    TextView tvDate;
    MaterialButton btPickDate,saveAppointment;

    private EditText editTextName,editText_app_phone,editTextEmail_app,editText_purpose_app;

    private MovieService movieService;

    Appointment appointment = new Appointment();
    SimpleDateFormat simpleDateFormat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_appointment);

        actionBar = getSupportActionBar(); // or getActionBar();
        actionBar.setTitle("Set Appointment");
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        movieService = MovieApi.getClient(SetAppointment.this).create(MovieService.class);


        tvDate = findViewById(R.id.tvDate);
        btPickDate = findViewById(R.id.btPickDate);
        saveAppointment = findViewById(R.id.saveAppointment);

        editTextName = (TextInputEditText) findViewById(R.id.editTextName);
        editText_app_phone = (TextInputEditText) findViewById(R.id.editText_app_phone);
        editTextEmail_app = (TextInputEditText) findViewById(R.id.editTextEmail_app);
        editText_purpose_app = (TextInputEditText) findViewById(R.id.editText_purpose_app);




        btPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Please note that use your package name here
                com.pkasemer.MyFamlinkApp.DatePicker mDatePickerDialogFragment;
                mDatePickerDialogFragment = new com.pkasemer.MyFamlinkApp.DatePicker();
                mDatePickerDialogFragment.show(getSupportFragmentManager(), "DATE PICK");
            }
        });

        saveAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Please note that use your package name here
                saveAppointmentToServer();
            }
        });
    }


    private Call<PostResponse> createAppointment() {
        return movieService.postAppointment(appointment);
    }

    private void saveAppointmentToServer(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving Name...");
        progressDialog.show();


        final String app_name = editTextName.getText().toString().trim();
        final String app_phone = editText_app_phone.getText().toString().trim();
        final String app_email = editTextEmail_app.getText().toString().trim();
        final String app_purpose = editText_purpose_app.getText().toString().trim();

        UserModel user = SharedPrefManager.getInstance(SetAppointment.this).getUser();

        appointment.setName(app_name);
        appointment.setPhone(app_phone);
        appointment.setEmail(app_email);
        appointment.setPurpose(app_purpose);
        appointment.setUserid(String.valueOf(user.getId()));


        createAppointment().enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, retrofit2.Response<PostResponse> response) {

                //set response body to match OrderResponse Model
                PostResponse postResponse = response.body();
                progressDialog.dismiss();

                //if orderResponses is not null
                if (postResponse != null) {

                    //if no error- that is error = false
                    if (!postResponse.getError()) {
                        Log.i("Appointment Success", postResponse.getMessage() + postResponse.getError());
                        //if there is a success
                        //storing the name to sqlite with status synced
                        new SweetAlertDialog(SetAppointment.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Booked")
                                .setContentText("Appointment set successfully")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {

                                        sDialog.dismissWithAnimation();

                                        Intent i = new Intent(SetAppointment.this, RootActivity.class);
                                        startActivity(i);
                                    }
                                }).show();

                    } else {
                        Log.i("Ress", "message: " + (postResponse.getMessage()));
                        Log.i("et", "error false: " + (postResponse.getError()));
//                        ShowOrderFailed();
                        new SweetAlertDialog(SetAppointment.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText("Try again")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {

                                        sDialog.dismissWithAnimation();

                                        Intent i = new Intent(SetAppointment.this, RootActivity.class);
                                        startActivity(i);
                                    }
                                }).show();

                    }


                } else {
                    Log.i("Referral Response null", "Order is null Try Again: ");

                    //if there is some error
                    //saving the name to sqlite with status unsynced

                    new SweetAlertDialog(SetAppointment.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("Try again")
                            .setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {

                                    sDialog.dismissWithAnimation();

                                    Intent i = new Intent(SetAppointment.this, RootActivity.class);
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

                new SweetAlertDialog(SetAppointment.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("Try again")
                        .setConfirmText("OK")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.dismissWithAnimation();

                                Intent i = new Intent(SetAppointment.this, RootActivity.class);
                                startActivity(i);
                            }
                        }).show();
            }
        });




    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(mCalendar.getTime());
        tvDate.setText(selectedDate);


        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aaa z");
        String dateTime = simpleDateFormat.format(mCalendar.getTime()).toString();
        appointment.setAppointmentDate(dateTime);

        tvDate.setVisibility(View.VISIBLE);
    }
}