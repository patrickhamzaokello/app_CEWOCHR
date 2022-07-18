package com.pkasemer.MyFamlinkApp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class SetAppointment extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    ActionBar actionBar;
    MaterialButton saveAppointment;
    EditText datevalue, timevalue;

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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        apiEndPoints = ApiBase.getClient(SetAppointment.this).create(ApiEndPoints.class);


        datevalue = findViewById(R.id.datevalue);
        timevalue = findViewById(R.id.timevalue);
        saveAppointment = findViewById(R.id.saveAppointment);

        editText_purpose_app = (TextInputEditText) findViewById(R.id.editText_purpose_app);


        datevalue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Please note that use your package name here
                AppointmentDatePicker mDatePickerDialogFragment;
                mDatePickerDialogFragment = new AppointmentDatePicker();
                mDatePickerDialogFragment.show(getSupportFragmentManager(), "DATE PICK");
            }
        });

        timevalue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SetAppointment.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        timevalue.setText(selectedHour + ":" + selectedMinute);
                        appointment.setTime(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.show();
            }
        });

        saveAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Please note that use your package name here
                final String app_purpose = editText_purpose_app.getText().toString();
                final String app_date = datevalue.getText().toString();
                final String app_time = timevalue.getText().toString();


                //validating inputs
                if (TextUtils.isEmpty(app_purpose)) {
                    editText_purpose_app.setError("Please enter appointment purpose");
                    editText_purpose_app.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(app_date)) {
                    datevalue.setError("Please set appointment Date");
                    datevalue.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(app_time)) {
                    timevalue.setError("Please set appointment Time");
                    timevalue.requestFocus();
                    return;
                }
                saveAppointmentToServer();
            }
        });
    }


    private Call<PostResponse> createAppointment() {
        return apiEndPoints.postAppointment(appointment);
    }

    private void saveAppointmentToServer() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Submitting Appointment...");
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
                                .setTitleText("Submitted")
                                .setContentText("Appointment has been sent successfully")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {

                                        sDialog.dismissWithAnimation();

                                        editText_purpose_app.setText("");
                                        datevalue.setText("");
                                        timevalue.setText("");

                                    }
                                }).show();

                    } else {
                        Log.i("Ress", "message: " + (postResponse.getMessage()));
                        Log.i("et", "error false: " + (postResponse.getError()));
//                        ShowOrderFailed();
                        new SweetAlertDialog(SetAppointment.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText(postResponse.getMessage() + "Try again")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {

                                        sDialog.dismissWithAnimation();

                                    }
                                }).show();

                    }


                } else {
                    Log.i("Appointment", "Appointment is null Try Again: ");

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

                                }
                            }).show();
                    return;

                }

            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                progressDialog.dismiss();

                t.printStackTrace();
                Log.i("Appointment Failed", "Appointment Failed Try Again: " + t);
                //on error storing the name to sqlite with status unsynced

                new SweetAlertDialog(SetAppointment.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("Try again")
                        .setConfirmText("OK")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.dismissWithAnimation();

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
        appointment.setAppointmentDate(selectedDate);
    }

}