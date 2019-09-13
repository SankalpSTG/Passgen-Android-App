package com.example.passgen;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiPassword
{
    @FormUrlEncoded
    @POST("add_password.php")
    Call<ResponseBody> add_password(
            @Field("unique_id") String unicid,
            @Field("auth_key") String authkey,
            @Field("device_id") String deviceid,
            @Field("website") String url,
            @Field("user_name") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("update_password.php")
    Call<ResponseBody> update_password(
            @Field("unique_id") String unicid,
            @Field("auth_key") String authkey,
            @Field("device_id") String deviceid,
            @Field("url") String url,
            @Field("user_name") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("delete.php")
    Call<ResponseBody> delete(
            @Field("unique_id") String unicid,
            @Field("auth_key") String authkey,
            @Field("device_id") String deviceid,
            @Field("url") String url,
            @Field("user_name") String username
    );

    @FormUrlEncoded
    @POST("search_password.php")
    Call<ResponseBody> search_user(
            @Field("unique_id") String unicid,
            @Field("auth_key") String authkey,
            @Field("device_id") String deviceid
    );

    @FormUrlEncoded
    @POST("search_password_url.php")
    Call<ResponseBody> search_user_url(
            @Field("unique_id") String unicid,
            @Field("auth_key") String authkey,
            @Field("device_id") String deviceid,
            @Field("url") String url
    );

}
