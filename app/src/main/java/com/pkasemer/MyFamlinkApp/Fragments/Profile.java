package com.pkasemer.MyFamlinkApp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.pkasemer.MyFamlinkApp.HelperClasses.SharedPrefManager;
import com.pkasemer.MyFamlinkApp.LoginMaterial;
import com.pkasemer.MyFamlinkApp.Models.UserModel;
import com.pkasemer.MyFamlinkApp.R;


public class Profile extends Fragment {


    TextView textViewUsername, textViewEmail;

    public Profile() {
        // Required empty public constructor
    }


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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //if the user is not logged in
        //starting the login activity
        if (!SharedPrefManager.getInstance(getContext()).isLoggedIn()) {
//            finish();
            startActivity(new Intent(getContext(), LoginMaterial.class));
        }


        textViewUsername = (TextView) view.findViewById(R.id.full_name);
        textViewEmail = (TextView) view.findViewById(R.id.email_text);


        //getting the current user
        UserModel userModel = SharedPrefManager.getInstance(getContext()).getUser();

        //setting the values to the textviews
        textViewUsername.setText(userModel.getUsername());
        textViewEmail.setText(userModel.getEmail());

        //when the user presses logout button
        //calling the logout method
        view.findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefManager.getInstance(getContext()).logout();

            }
        });



        return view;
    }
}