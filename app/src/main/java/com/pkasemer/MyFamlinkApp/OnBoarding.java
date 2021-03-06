package com.pkasemer.MyFamlinkApp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.pkasemer.MyFamlinkApp.Adapters.OnboardingSliderAdapter;

public class OnBoarding extends AppCompatActivity {


        //Variables
        ViewPager viewPager;
        LinearLayout dotsLayout;
        OnboardingSliderAdapter onboardingSliderAdapter;
        TextView[] dots;
        Button letsGetStarted;
        Animation animation;
        int currentPos;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_on_boarding);

            ActionBar actionBar = getSupportActionBar(); // or getActionBar();
            getSupportActionBar().setTitle("My new title"); // set the top title
            String title = actionBar.getTitle().toString(); // get the title
            actionBar.hide();

            //Hooks
            viewPager = findViewById(R.id.slider);
            dotsLayout = findViewById(R.id.dots);
            letsGetStarted = findViewById(R.id.get_started_btn);

            //Call adapter
            onboardingSliderAdapter = new OnboardingSliderAdapter(this);
            viewPager.setAdapter(onboardingSliderAdapter);

            //Dots
            addDots(0);
            viewPager.addOnPageChangeListener(changeListener);



            animation = AnimationUtils.loadAnimation(OnBoarding.this, R.anim.bottom_anim);
            letsGetStarted.setAnimation(animation);

            findViewById(R.id.get_started_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(OnBoarding.this, RegisterMaterial.class);
                    startActivity(intent);
                    finish();
                }
            });
        }

        public void skip(View view) {
            startActivity(new Intent(this, LoginMaterial.class));
            finish();
        }

        public void next(View view) {
            viewPager.setCurrentItem(currentPos + 1);
        }

        private void addDots(int position) {

            dots = new TextView[4];
            dotsLayout.removeAllViews();

            for (int i = 0; i < dots.length; i++) {
                dots[i] = new TextView(this);
                dots[i].setText(Html.fromHtml("."));
                dots[i].setTextSize(58);
                dots[i].setTextColor(getResources().getColor(R.color.purple_200));
                dots[i].setPadding(0,0,0,0);
                dots[i].setIncludeFontPadding(false);
                dotsLayout.addView(dots[i]);
            }

            if (dots.length > 0) {
                dots[position].setTextColor(getResources().getColor(R.color.buttonRed));
            }

        }

        ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addDots(position);
                currentPos = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
    }

