package com.pkasemer.MyFamlinkApp.Apis;

import com.pkasemer.MyFamlinkApp.Models.FoodDBModel;
import com.pkasemer.MyFamlinkApp.Models.HomeBannerModel;
import com.pkasemer.MyFamlinkApp.Models.HomeMenuCategoryModel;
import com.pkasemer.MyFamlinkApp.Models.SectionedCategoryMenu;
import com.pkasemer.MyFamlinkApp.Models.SelectedCategoryMenuItem;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface MovieService {

    @GET("menus/menupages.php")
    Call<SelectedCategoryMenuItem> getTopRatedMovies(
            @Query("category") int menu_category_id,
            @Query("page") int pageIndex
    );

    @GET("menus/topmenuitems.php")
    Call<SelectedCategoryMenuItem> getTopMenuItems(
            @Query("page") int pageIndex
    );

    @GET("menucategory/readPaginated.php")
    Call<HomeMenuCategoryModel> getMenuCategories();

    @GET("menucategory/readSectionedMenu.php")
    Call<SectionedCategoryMenu> getMenuCategoriesSection();

    @GET("banner/read.php")
    Call<HomeBannerModel> getHomeBanners();

//    menus/menudetails.php?menuId=9&category=3&page=1
    @GET("menus/menudetails.php")
    Call<SelectedCategoryMenuItem> getMenuDetails(
            @Query("menuId") int menu_id,
            @Query("category") int menu_category_id,
            @Query("page") int pageIndex
    );



    @POST("menus/create.php")
    Call<FoodDBModel> postCartItems(
        @Body FoodDBModel foodDBModel
    );

}