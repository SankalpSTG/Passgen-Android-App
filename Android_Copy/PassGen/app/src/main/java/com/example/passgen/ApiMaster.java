package com.example.passgen;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiMaster
{
    @FormUrlEncoded
    @POST("register.php")
    Call<ResponseBody> register(
            @Field("user_name") String username,
            @Field("unique_id") String unicid,
            @Field("auth_key") String authkey,
            @Field("device_id") String deviceid,
            @Field("master_password") String masterpassword,
            @Field("secret_question") String secret_question,
            @Field("secret_answer") String secretanswer
    );

    @FormUrlEncoded
    @POST("login.php")
    Call<ResponseBody> login(
            @Field("unique_id") String unicid,
            @Field("auth_key") String authkey,
            @Field("device_id") String deviceid,
            @Field("master_password") String masterpassword
    );

    @FormUrlEncoded
    @POST("update_master_password.php")
    Call<ResponseBody> updateMaserPassword(
            @Field("unique_id") String unicid,
            @Field("device_id") String deviceid,
            @Field("auth_key") String authkey,
            @Field("master_password") String masterpassword
    );
    @FormUrlEncoded
    @POST("share_access.php")
    Call<ResponseBody> share_access(
            @Field("unique_id") String unicid,
            @Field("device_id") String deviceid,
            @Field("auth_key") String authkey
    );
}
