package com.pkasemer.MyFamlinkApp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.TimeFormat;
import com.pkasemer.MyFamlinkApp.Apis.ApiBase;
import com.pkasemer.MyFamlinkApp.Apis.ApiEndPoints;
import com.pkasemer.MyFamlinkApp.HelperClasses.SharedPrefManager;
import com.pkasemer.MyFamlinkApp.Models.Appointment;
import com.pkasemer.MyFamlinkApp.Models.PostResponse;
import com.pkasemer.MyFamlinkApp.Models.UserModel;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;

public class SetAppointment extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    ActionBar actionBar;
    TextView datevalue,timevalue;
    MaterialButton btPickDate,pickTime,saveAppointment;


    private EditText editText_purpose_app;

    private ApiEndPoints apiEndPoints;

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

        apiEndPoints = ApiBase.getClient(SetAppointment.this).create(ApiEndPoints.class);


        datevalue = findViewById(R.id.datevalue);
        timevalue = findViewById(R.id.timevalue);
        btPickDate = findViewById(R.id.btPickDate);
        pickTime = findViewById(R.id.pickTime);
        saveAppointment = findViewById(R.id.saveAppointment);

        editText_purpose_app = (TextInputEditText) findViewById(R.id.editText_purpose_app);




        btPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Please note that use your package name here
                AppointmentDatePicker mDatePickerDialogFragment;
                mDatePickerDialogFragment = new AppointmentDatePicker();
                mDatePickerDialogFragment.show(getSupportFragmentManager(), "DATE PICK");
            }
        });

        pickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Please note that use your package name here
                AppointmentTimePicker appointmentTimePicker;
                appointmentTimePicker = new AppointmentTimePicker();
                appointmentTimePicker.show(getSupportFragmentManager(), "Pick Time");
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
        return apiEndPoints.postAppointment(appointment);
    }

    private void saveAppointmentToServer(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving Name...");
        progressDialog.show();

        final String app_purpose = editText_purpose_app.getText().toString().trim();

        UserModel user = SharedPrefManager.getInstance(SetAppointment.this).getUser();


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
        datevalue.setText(selectedDate);


        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd aaa z");
        String dateTime = simpleDateFormat.format(mCalendar.getTime()).toString();
        appointment.setAppointmentDate(dateTime);

        datevalue.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.HOUR, hour);
        mCalendar.set(Calendar.MINUTE, minute);

        simpleDateFormat = new SimpleDateFormat("hh:mm:ss aaa ");
        String selectedTime = simpleDateFormat.format(mCalendar.getTime()).toString();
//        appointment.setAppointmentDate(dateTime);
        timevalue.setText(selectedTime);

//        appointment.setAppointmentDate(dateTime);

        timevalue.setVisibility(View.VISIBLE);
    }
}