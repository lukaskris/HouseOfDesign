package com.example.lukaskris.houseofdesign.Services;

import com.example.lukaskris.houseofdesign.Model.Customer;
import com.example.lukaskris.houseofdesign.Model.Items;
import com.example.lukaskris.houseofdesign.Model.SubItem;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by xub on 28/08/17.
 */

public interface MyService {
    String SERVICE_ENDPOINT = "https://house-of-design.herokuapp.com/api/";
    String LOCAL_ENDPOINT = "http://10.120.120.25:3000/api/";

    @GET("category")
    Observable<List<SubItem>> getCategory();

    @GET("item/{category}/{offset}/{limit}")
    Observable<List<Items>> getItems(@Path("category") int category, @Path("offset") int offset, @Path("limit") int limit);

    @GET("category/{id}")
    Observable<List<SubItem>> getSubItems(@Path("id") int item_id);

    @POST("customer")
    Observable<Customer> createCustomer(@Body Customer customer);

}
