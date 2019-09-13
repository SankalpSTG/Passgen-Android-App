package com.smartn.passgen;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient
{
    private static final String
            BASE_URL="http://192.168.16.111/passgen/api/v3/";
    private static RetrofitClient mInstance;
    private Retrofit retrofit;

    private RetrofitClient()
    {
        retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    public static synchronized RetrofitClient getInstance()
    {
        if (mInstance==null)
        {
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }

    public ApiMaster getApiMaster()
    {
        return retrofit.create(ApiMaster.class);
    }

    public ApiPassword getApiPassword() {
        return retrofit.create(ApiPassword.class);
    }
}

