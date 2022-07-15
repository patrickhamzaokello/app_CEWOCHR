package com.pkasemer.MyFamlinkApp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pkasemer.MyFamlinkApp.HelperClasses.SharedPrefManager;
import com.pkasemer.MyFamlinkApp.HttpRequests.RequestHandler;
import com.pkasemer.MyFamlinkApp.HttpRequests.URLs;
import com.pkasemer.MyFamlinkApp.Models.UserModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegisterMaterial extends AppCompatActivity {

    TextView logoText;
    Button callLogIN, register_btn;

    TextInputEditText inputTextFullname, inputTextEmail, inputTextPhone, inputTextAddress, inputTextPassword, inputTextConfirmPassword;
    RadioGroup radioGroupGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register_material);

        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        actionBar.hide();

        //if the user is already logged in we will directly start the profile activity
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, RootActivity.class));
            return;
        }


        //Hooks

        //Hooks
        callLogIN = findViewById(R.id.login_screen);
        register_btn = findViewById(R.id.register_btn);
        logoText = findViewById(R.id.register_welcomeback);


        //get text from input boxes
        inputTextFullname = findViewById(R.id.inputTextFullname);
        inputTextEmail = findViewById(R.id.inputTextEmail);
        inputTextPhone = findViewById(R.id.inputTextPhone);
        inputTextAddress = findViewById(R.id.inputTextAddress);
        inputTextPassword = findViewById(R.id.inputTextPassword);
        inputTextConfirmPassword = findViewById(R.id.inputTextConfirmPassword);

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        callLogIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterMaterial.this, LoginMaterial.class);
                startActivity(intent);
            }
        });


    }


    private void registerUser() {
        final String full_name = inputTextFullname.getText().toString().trim();
        final String user_email = inputTextEmail.getText().toString().trim();
        final String user_phone = inputTextPhone.getText().toString().trim();
        final String user_address = inputTextAddress.getText().toString().trim();
        final String user_password = inputTextPassword.getText().toString().trim();
        final String confirm_password = inputTextConfirmPassword.getText().toString().trim();


        if (TextUtils.isEmpty(full_name)) {
            inputTextFullname.setError("Enter Full Name");
            inputTextFullname.requestFocus();
            return;
        }


        if (!(TextUtils.isEmpty(user_email))) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(user_email).matches()) {
                inputTextEmail.setError("Enter a valid email");
                inputTextEmail.requestFocus();
                return;
            }
        }

        if (TextUtils.isEmpty(user_phone)) {
            inputTextPhone.setError("Enter a Phone Number");
            inputTextPhone.requestFocus();
            return;
        }

        if (user_phone.length() > 10) {
            inputTextPhone.setError("Phone Number is invalid, Use format 07xxxxxxxx");
            inputTextPhone.requestFocus();
            return;
        }
        if (user_phone.length() < 10) {
            inputTextPhone.setError("Enter a valid 10 digit Phone Number");
            inputTextPhone.requestFocus();
            return;
        }


        if (TextUtils.isEmpty(user_address)) {
            inputTextAddress.setError("Enter you Address");
            inputTextAddress.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(user_password)) {
            inputTextPassword.setError("Enter a password");
            inputTextPassword.requestFocus();
            return;
        }

        if (!user_password.equals(confirm_password)) {
            inputTextPassword.setError("Password Does not Match");
            inputTextPassword.requestFocus();
            return;
        }


        //if it passes all the validations

        class RegisterUser extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("full_name", full_name);
                params.put("email", user_email);
                params.put("phone_number", user_phone);
                params.put("password", user_password);
                params.put("location_address", user_address);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_REGISTER, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //displaying the progress bar while user registers on the server
                progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //hiding the progressbar after completion
                progressBar.setVisibility(View.GONE);

                if (s.isEmpty()) {
                    //show network error
                    showErrorAlert();
                    return;
                }


                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {

                        //getting the user from the response
                        JSONObject userJson = obj.getJSONObject("user");

                        //creating a new user object
                        UserModel userModel = new UserModel(
                                userJson.getInt("id"),
                                userJson.getString("fullname"),
                                userJson.getString("email"),
                                userJson.getString("phone"),
                                userJson.getString("address"),
                                userJson.getString("profileimage")
                        );

                        //storing the user in shared preferences
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(userModel);

                        //starting the profile activity
                        finish();
                        startActivity(new Intent(getApplicationContext(), RootActivity.class));
                    } else {
                        showUserExists(obj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //executing the async task
        RegisterUser ru = new RegisterUser();
        ru.execute();
    }


    private void showErrorAlert() {
        new SweetAlertDialog(
                this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText("Something went wrong!")
                .show();
    }

    private void showUserExists( String message) {

        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error")
                .setContentText(message)
                .show();
    }

}