package com.pkasemer.MyFamlinkApp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pkasemer.MyFamlinkApp.Adapters.ReportsAdapter;
import com.pkasemer.MyFamlinkApp.HelperClasses.ReportsInterface;
import com.pkasemer.MyFamlinkApp.Models.Case;
import com.pkasemer.MyFamlinkApp.Utils.NetworkStateChecker;
import com.pkasemer.MyFamlinkApp.localDatabase.DatabaseHelper;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class  AllReports extends AppCompatActivity {

    private DatabaseHelper db;
    List<Case> caseList;
    private ProgressBar progressBar;
    ReportsAdapter reportsAdapter;
    RecyclerView recyclerView;

    ActionBar actionBar;

    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "net.simplifiedcoding.datasaved";
    //Broadcast receiver to know the sync status
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reports);
        actionBar = getSupportActionBar(); // or getActionBar();
        actionBar.setTitle("All Reports");
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        progressBar = findViewById(R.id.reports_main_progress);
        recyclerView = findViewById(R.id.reports_main_recycler);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        db = new DatabaseHelper(this);
        caseList = new ArrayList<>();


        registerReceiver(new NetworkStateChecker(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        //calling the method to load all the stored names
        loadReports();

        //the broadcast receiver to update sync status
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //loading the names again
                loadReports();
            }
        };

        //registering the broadcast receiver to update sync status
        registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));

    }


    private void loadReports() {
        caseList.clear();
        caseList = db.list_DB_Cases();
        if (caseList.size() > 0) {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            reportsAdapter = new ReportsAdapter(this, caseList);
            recyclerView.setAdapter(reportsAdapter);
            reportsAdapter.notifyDataSetChanged();
        } else {
            recyclerView.setVisibility(View.GONE);
            emptycartwarning();
            return;
        }

    }

    private void emptycartwarning() {
        new SweetAlertDialog(AllReports.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("No Record Existing")
                .setContentText("Create a new Record")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        Intent i = new Intent(AllReports.this, RootActivity.class);
                        startActivity(i);
                    }
                }).show();
    }

    private void refreshList() {
        reportsAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



}