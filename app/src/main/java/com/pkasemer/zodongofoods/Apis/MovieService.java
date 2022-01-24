package com.pkasemer.zodongofoods.Apis;

import com.pkasemer.zodongofoods.Models.HomeBannerModel;
import com.pkasemer.zodongofoods.Models.HomeMenuCategoryModel;
import com.pkasemer.zodongofoods.Models.SectionedCategoryMenu;
import com.pkasemer.zodongofoods.Models.SelectedCategoryMenuItem;

import retrofit2.Call;
import retrofit2.http.GET;
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

}