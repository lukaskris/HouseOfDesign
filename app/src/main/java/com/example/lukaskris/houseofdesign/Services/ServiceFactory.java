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
//    public static <T> T createRetrofitService(final Class<T> clazz, final String endPoint) {
//        final RestAdapter restAdapter = new RestAdapter.Builder()
//                .setEndpoint(endPoint)
//                .build();
//        T service = restAdapter.create(clazz);
//
//        return service;
//    }
    public static MyService service = new Retrofit.Builder()
        .baseUrl(MyService.SERVICE_ENDPOINT)
        .client(new OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build().create(MyService.class);



}
