package com.example.lukaskris.houseofdesign.Services;

import com.example.lukaskris.houseofdesign.Model.Category;
import com.example.lukaskris.houseofdesign.Model.Customer;
import com.example.lukaskris.houseofdesign.Model.Items;
import com.example.lukaskris.houseofdesign.Model.Orders;
import com.example.lukaskris.houseofdesign.Model.OrdersDetail;
import com.example.lukaskris.houseofdesign.Model.OrdersInfo;
import com.example.lukaskris.houseofdesign.Model.ShippingAddress;
import com.example.lukaskris.houseofdesign.Model.SubItem;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by xub on 28/08/17.
 */

public interface MyService {
    String SERVICE_ENDPOINT = "http://house-of-design.herokuapp.com/api/";
    String LOCAL_ENDPOINT = "http://10.120.120.25:3000/api/";
    String HOTSPOT_ENDPOINT = "http://192.168.43.249:3000/api/";

    @GET("category")
    Observable<List<Category>> getCategory();

    @GET("item/{category}/{offset}/{limit}")
    Observable<List<Items>> getItems(@Path("category") int category, @Path("offset") int offset, @Path("limit") int limit);

    @GET("items")
    Observable<List<Items>> getItems();

    @GET("sub_item/{id}")
    Observable<List<SubItem>> getSubItems(@Path("id") int item_id);

    @POST("customer")
    Observable<Customer> createCustomer(@Body Customer customer);

    @GET("customer/{id}")
    Observable<Customer> getCustomer(@Path("id") String email);

    @POST("shipping_address")
    Observable<ShippingAddress> createAddress(@Body ShippingAddress shippingAddress);

    @GET("shipping_address/{id}")
    Observable<List<ShippingAddress>> getAddress(@Path("id") String email);

    @GET("shipping_address/{email}/{id}")
    Observable<ShippingAddress> getAddress(@Path("email") String email, @Path("id") String id);

    @GET("default_shipping_address/{id}")
    Observable<List<ShippingAddress>> getDefaultAddress(@Path("id") String email);

    @DELETE("shipping_address/{id}")
    Observable<ShippingAddress> deleteAddress(@Path("id") String id);

    @POST("shipping_address/{id}")
    Observable<List<ShippingAddress>> setDefaultAddress(@Path("id") String id, @Body ShippingAddress shippingAddress);

    @POST("orders")
    Observable<Orders> setOrders(@Body Orders orders);

    @GET("orders/{email}/{offset}/{limit}/{filter}/{search}")
    Observable<List<Orders>> getOrders(@Path("email")String email, @Path("search")String search, @Path("filter")String filter, @Path("offset") int offset, @Path("limit") int limit);

    @GET("orders_pembelian/{email}/{offset}/{limit}/{filter}/{search}")
    Observable<List<Orders>> getOrdersPembelian(@Path("email")String email, @Path("search")String search, @Path("filter")String filter, @Path("offset") int offset, @Path("limit") int limit);

    @POST("orders_detail")
    Observable<OrdersDetail> setOrdersDetail(@Body OrdersDetail ordersDetail);

    @GET("orders_detail/{invoice}")
    Observable<List<OrdersDetail>> getOrdersDetail(@Path("invoice") String invoice);

    @GET("orders_info/{invoice}")
    Observable<List<OrdersInfo>> getOrdersInfo(@Path("invoice") String invoice);

    @POST("orders_info")
    Observable<OrdersInfo> setOrdersInfo(@Body OrdersInfo ordersInfo);
}
