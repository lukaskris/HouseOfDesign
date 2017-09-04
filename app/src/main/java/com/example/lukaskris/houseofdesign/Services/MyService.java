package com.example.lukaskris.houseofdesign.Services;

import com.example.lukaskris.houseofdesign.Model.Category;
import com.example.lukaskris.houseofdesign.Model.Customer;
import com.example.lukaskris.houseofdesign.Model.Items;
import com.example.lukaskris.houseofdesign.Model.ShippingAddress;
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
    String SERVICE_ENDPOINT = "http://house-of-design.herokuapp.com/api/";
    String LOCAL_ENDPOINT = "http://10.120.120.25:3000/api/";


    @GET("category")
    Observable<List<Category>> getCategory();

    @GET("item/{category}/{offset}/{limit}")
    Observable<List<Items>> getItems(@Path("category") int category, @Path("offset") int offset, @Path("limit") int limit);

    @GET("sub_item/{id}")
    Observable<List<SubItem>> getSubItems(@Path("id") int item_id);

    @POST("customer")
    Observable<Customer> createCustomer(@Body Customer customer);

    @GET("customer/{id}")
    Observable<List<Customer>> getCustomer(@Path("id") String email);

    @POST("shipping_address")
    Observable<ShippingAddress> createAddress(@Body ShippingAddress shippingAddress);

    @GET("shipping_address/{id}")
    Observable<List<ShippingAddress>> getAddress(@Path("id") String email);

    @POST("shipping_address/{id}")
    Observable<List<ShippingAddress>> setDefaultAddress(@Path("id") String id, @Body ShippingAddress shippingAddress);
}
