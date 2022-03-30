package com.pkasemer.MyFamlinkApp.Apis;


import com.pkasemer.MyFamlinkApp.Models.PostResponse;
import com.pkasemer.MyFamlinkApp.Models.Referal;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface MovieService {

//    http://192.168.88.190:8080/projects/myfamLinkApp/mobile/api/v1/refercase/create_case.php
    //post refer case
    @POST("refercase/create_case.php")
    @Headers("Cache-Control: no-cache")
    Call<PostResponse> postReferCase(
            @Body Referal createAddress
    );
}