package com.pkasemer.MyFamlinkApp.Apis;

import com.pkasemer.MyFamlinkApp.Models.HomeBannerModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface MovieService {

    @GET("banner/read.php")
    Call<HomeBannerModel> getHomeBanners();

}