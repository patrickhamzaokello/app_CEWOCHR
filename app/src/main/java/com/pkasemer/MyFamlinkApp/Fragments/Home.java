package com.pkasemer.MyFamlinkApp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.pkasemer.MyFamlinkApp.SetAppointment;
import com.pkasemer.MyFamlinkApp.AllReports;
import com.pkasemer.MyFamlinkApp.Apis.ApiBase;
import com.pkasemer.MyFamlinkApp.Apis.ApiEndPoints;
import com.pkasemer.MyFamlinkApp.AboutUs;
import com.pkasemer.MyFamlinkApp.R;
import com.pkasemer.MyFamlinkApp.ReportChild;
import com.smarteist.autoimageslider.SliderView;


public class Home extends Fragment {

    public Home() {
        // Required empty public constructor
    }

    private static final String TAG = "MainActivity";
    SliderView sliderView;
    CardView welcome_card_layout, addChildCard, allReports, findFamily, addFamily;

    private ApiEndPoints apiEndPoints;


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
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        sliderView = view.findViewById(R.id.home_slider);
        welcome_card_layout = view.findViewById(R.id.welcome_card_layout);
        addChildCard = view.findViewById(R.id.addChildCard);
        allReports = view.findViewById(R.id.allReports);
        findFamily = view.findViewById(R.id.findFamily);
        addFamily = view.findViewById(R.id.addFamily);
        //init service and load data
        apiEndPoints = ApiBase.getClient(getContext()).create(ApiEndPoints.class);


        addChildCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), ReportChild.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(i);
            }
        });

        allReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), AllReports.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(i);
            }
        });
        addFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), SetAppointment.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(i);
            }
        });
        findFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), AboutUs.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

    }


}