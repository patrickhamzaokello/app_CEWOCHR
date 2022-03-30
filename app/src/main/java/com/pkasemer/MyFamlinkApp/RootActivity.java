package com.pkasemer.MyFamlinkApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pkasemer.MyFamlinkApp.localDatabase.SenseDBHelper;

public class RootActivity extends AppCompatActivity {

    BottomNavigationView navView;
    SenseDBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_activity);

        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        getSupportActionBar().setTitle("My new title"); // set the top title
        String title = actionBar.getTitle().toString(); // get the title
        actionBar.hide();



        //Initialize Bottom Navigation View.
        navView = findViewById(R.id.bottomNav_view);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMasage,new IntentFilter(getString(R.string.cartcoutAction)));

        db = new SenseDBHelper(this);

        //Pass the ID's of Different destinations
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,  R.id.navigation_message, R.id.navigation_profile )
                .build();

        //Initialize NavController.
        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(navView, navController);
        navView.getOrCreateBadge(R.id.navigation_message).setBackgroundColor(getResources().getColor(R.color.sweetRed));

        //updating cart counts
        updatecartCount();

    }


    public void switchContent(int id, Fragment fragment, String fragmentname) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id, fragment, fragment.toString());
//        ft.addToBackStack(fragmentname);
        ft.commit();
    }


    public BroadcastReceiver mMasage=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String cartcount=intent.getStringExtra(getString(R.string.cartCount));

            if((Integer.parseInt(cartcount)) != 0){
                navView.getOrCreateBadge(R.id.navigation_message).setNumber(Integer.parseInt(cartcount));
                navView.getOrCreateBadge(R.id.navigation_message).setVisible(true);

            }else {
                navView.getOrCreateBadge(R.id.navigation_message).clearNumber();
                navView.getOrCreateBadge(R.id.navigation_message).setVisible(false);

            }
        }
    };

    private void updatecartCount() {
        int mycartcount = db.countCart();
        if(mycartcount != 0){
            navView.getOrCreateBadge(R.id.navigation_message).setNumber(mycartcount);
            navView.getOrCreateBadge(R.id.navigation_message).setVisible(true);

        }else {
            navView.getOrCreateBadge(R.id.navigation_message).clearNumber();
            navView.getOrCreateBadge(R.id.navigation_message).setVisible(false);
        }
    }



}