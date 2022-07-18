package com.pkasemer.MyFamlinkApp.HttpRequests;
/**
 * Created by Belal on 9/5/2017.
 */

public class URLs {

    private static final String ROOT_URL = "http://192.168.0.107:8080/projects/myfamLinkApp/mobile/api/v1/";
//    private static final  String ROOT_URL = "http://famlink.kakebe.com/mobile/api/v1/";
    public static final String URL_REGISTER = ROOT_URL + "users/account?apicall=signup";
    public static final String URL_LOGIN= ROOT_URL + "users/account.php?apicall=login";
    public static final String URL_SAVE_NAME =  ROOT_URL + "refercase/addChild.php";


}
