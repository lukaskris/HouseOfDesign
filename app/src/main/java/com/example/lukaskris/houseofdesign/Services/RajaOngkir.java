package com.example.lukaskris.houseofdesign.Services;

import com.rx2androidnetworking.Rx2AndroidNetworking;

import org.json.JSONObject;

import io.reactivex.Observable;

/**
 * Created by lukaskris on 9/8/2017.
 */

public class RajaOngkir {
    static String key = "e1b42829f5d45bf380bf7f22aa57cb06";
    public static Observable<JSONObject> getProvince(){
        return Rx2AndroidNetworking.get("https://pro.rajaongkir.com/api/province")
                .addHeaders("key",key)
                .build()
                .getJSONObjectObservable();
    }

    public static Observable<JSONObject> getCity(String province){
        return Rx2AndroidNetworking.get("https://pro.rajaongkir.com/api/city?province={province}")
                .addHeaders("key",key)
                .addPathParameter("province",province)
                .build()
                .getJSONObjectObservable();
    }

    public static Observable<JSONObject> getSubdistrict(String city){
        return Rx2AndroidNetworking.get("https://pro.rajaongkir.com/api/subdistrict?city={city}")
                .addHeaders("key",key)
                .addPathParameter("city",city)
                .build()
                .getJSONObjectObservable();
    }

    public static Observable<JSONObject> getCost(String origin, String destination, int weight, String courier){
        return Rx2AndroidNetworking.post("https://pro.rajaongkir.com/api/cost")
                .addHeaders("key",key)
                .addBodyParameter("origin",origin)
                .addBodyParameter("originType","city")
                .addBodyParameter("destination",destination)
                .addBodyParameter("destinationType","subdistrict")
                .addBodyParameter("weight", String.valueOf(weight))
                .addBodyParameter("courier",courier)
                .build()
                .getJSONObjectObservable();
    }
}
