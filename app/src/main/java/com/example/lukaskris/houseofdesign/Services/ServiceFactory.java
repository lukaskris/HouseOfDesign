package com.example.lukaskris.houseofdesign.Services;


//import retrofit.RestAdapter;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by xub on 29/08/17.
 */

public class ServiceFactory {

    public static MyService service = new Retrofit.Builder()
        .baseUrl(MyService.HOTSPOT_ENDPOINT)
        .client(new OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build().create(MyService.class);



}
