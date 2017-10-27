package com.leaksoft.app.houseofdesign.services;

import com.leaksoft.app.houseofdesign.model.Category;
import com.leaksoft.app.houseofdesign.model.Customer;
import com.leaksoft.app.houseofdesign.model.Items;
import com.leaksoft.app.houseofdesign.model.Orders;
import com.leaksoft.app.houseofdesign.model.OrdersBukti;
import com.leaksoft.app.houseofdesign.model.OrdersDetail;
import com.leaksoft.app.houseofdesign.model.OrdersInfo;
import com.leaksoft.app.houseofdesign.model.ShippingAddress;
import com.leaksoft.app.houseofdesign.model.SubItem;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by xub on 28/08/17.
 */

public interface MyService {
    String SERVICE_ENDPOINT = "http://house-of-design.herokuapp.com/api/";
    String LOCAL_ENDPOINT = "http://10.0.2.2:3000/api/";
    String HOTSPOT_ENDPOINT = "http://192.168.43.249:3000/api/";

    @GET("category")
    Observable<List<Category>> getCategory();

    @GET("item/{category}/{offset}/{limit}/{filter}/{order}")
    Observable<List<Items>> getItems(@Path("category") int category, @Path("offset") int offset, @Path("limit") int limit,@Path("filter") String filter, @Path("order") String order);

    @GET("search_item/{key}/{offset}/{limit}")
    Observable<List<Items>> getItems(@Path("key") String key, @Path("offset") int offset, @Path("limit") int limit);

    @GET("search_item/{key}/{offset}/{limit}/{filter}/{order}")
    Observable<List<Items>> getItems(@Path("key") String key, @Path("offset") int offset, @Path("limit") int limit,@Path("filter") String filter, @Path("order") String order);

    @GET("items")
    Observable<List<Items>> getItems();

    @GET("sub_item/{id}")
    Observable<List<SubItem>> getSubItems(@Path("id") int item_id);

    @POST("customer")
    Observable<Customer> createCustomer(@Body Customer customer);

    @GET("customer/{id}")
    Observable<Customer> getCustomer(@Path("id") String email);

    @PUT("profile")
    Observable<Customer> updateProfile(@Body Customer customer);

    @PUT("token")
    Observable<Customer> updateToken(@Body Customer customer);

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

    @PUT("orders")
    Observable<Orders> updateOrder(@Body Orders orders);

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

    @POST("orders_bukti")
    Observable<OrdersBukti> setOrdersBukti(@Body OrdersBukti ordersBukti);

    @GET("search/{key}")
    Observable<List<Items>> getSearch(@Path("key") String key);
}
