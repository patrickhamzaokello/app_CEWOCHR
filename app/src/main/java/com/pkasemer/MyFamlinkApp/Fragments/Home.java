package com.pkasemer.MyFamlinkApp.Fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.pkasemer.MyFamlinkApp.Adapters.HomeSliderAdapter;
import com.pkasemer.MyFamlinkApp.AddFamily;
import com.pkasemer.MyFamlinkApp.AllReports;
import com.pkasemer.MyFamlinkApp.Apis.MovieApi;
import com.pkasemer.MyFamlinkApp.Apis.MovieService;
import com.pkasemer.MyFamlinkApp.FindFamily;
import com.pkasemer.MyFamlinkApp.Models.Banner;
import com.pkasemer.MyFamlinkApp.Models.HomeBannerModel;
import com.pkasemer.MyFamlinkApp.R;
import com.pkasemer.MyFamlinkApp.ReportChild;
import com.smarteist.autoimageslider.SliderView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Home extends Fragment  {

    public Home() {
        // Required empty public constructor
    }

    private static final String TAG = "MainActivity";
    SliderView sliderView;
    CardView welcome_card_layout, addChildCard,allReports,findFamily,addFamily;

    List<Banner> banners;
    private MovieService movieService;




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
        movieService = MovieApi.getClient(getContext()).create(MovieService.class);


        addChildCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), ReportChild.class);
                startActivity(i);
            }
        });

        allReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), AllReports.class);
                startActivity(i);
            }
        });
        addFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), AddFamily.class);
                startActivity(i);
            }
        });
        findFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), FindFamily.class);
                startActivity(i);
            }
        });
        


        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
        loadFirstPage();

    }



    private void loadFirstPage() {

        class LoadFirstPage extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                homeslider();
            }

            @Override
            protected String doInBackground(Void... voids) {
                return "done";
            }
        }

        LoadFirstPage ulLoadFirstPage = new LoadFirstPage();
        ulLoadFirstPage.execute();
    }



    public void homeslider() {

        callGetHomeBanners().enqueue(new Callback<HomeBannerModel>() {
            @Override
            public void onResponse(Call<HomeBannerModel> call, Response<HomeBannerModel> response) {
                // Got data. Send it to adapter
                banners = fetchBannerResults(response);
                if (banners.isEmpty()) {
                    return;
                } else {
                    // initializing the slider view.
                    sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
                    sliderView.setScrollTimeInSec(3);
                    sliderView.setAutoCycle(true);
                    sliderView.startAutoCycle();
                    // passing this array list inside our adapter class.
                    HomeSliderAdapter adapter = new HomeSliderAdapter(getContext(), banners);
                    sliderView.setSliderAdapter(adapter);
                }

            }

            @Override
            public void onFailure(Call<HomeBannerModel> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }




    private List<Banner> fetchBannerResults(Response<HomeBannerModel> response) {
        HomeBannerModel homeBannerModel = response.body();
        int TOTAL_PAGES = homeBannerModel.getTotalPages();
        System.out.println("total pages banners" + TOTAL_PAGES);
        return homeBannerModel.getBanners();
    }


    /**
     * Performs a Retrofit call to the callGetMenuCategoriesApi API.
     * Same API call for Pagination.
     */
    private Call<HomeBannerModel> callGetHomeBanners() {
        return movieService.getHomeBanners();
    }














}