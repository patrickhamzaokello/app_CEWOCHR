package com.pkasemer.MyFamlinkApp.Apis;


import com.pkasemer.MyFamlinkApp.Models.Appointment;
import com.pkasemer.MyFamlinkApp.Models.PostResponse;
import com.pkasemer.MyFamlinkApp.Models.Referal;
import com.pkasemer.MyFamlinkApp.Models.UserFeedback;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiEndPoints {

    //post refer case

    //    http://192.168.88.190:8080/projects/myfamLinkApp/mobile/api/v1/refercase/create_case.php
    @POST("routes/create_case.php")
    @Headers("Cache-Control: no-cache")
    Call<PostResponse> postReferCase(
            @Body Referal createCase
    );

    //post appointment

//    http://192.168.88.190:8080/projects/myfamLinkApp/mobile/api/v1/routes/create_appointment.php
    @POST("routes/create_appointment.php")
    @Headers("Cache-Control: no-cache")
    Call<PostResponse> postAppointment(
            @Body Appointment createAppointment
    );

    //fetch past orders
//    http://localhost:8080/projects/myfamLinkApp/mobile/api/v1/routes/userFeedbacks.php?userID=1&page=1
    @GET("routes/userFeedbacks.php")
    @Headers("Cache-Control: no-cache")
    Call<UserFeedback> getUserOrders(
            @Query("userID") int customerID,
            @Query("page") int pageIndex
    );

}