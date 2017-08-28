package com.example.lukaskris.houseofdesign.Services;

import com.example.lukaskris.houseofdesign.Model.Item;

import io.reactivex.Observable;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by xub on 28/08/17.
 */

public class MyService {
    String SERVICE_ENDPOINT = "https://api.github.com";

    @GET("/users/{login}")
    Observable<Item> getUser(@Path("login") String login) {
        return null;
    }

}
