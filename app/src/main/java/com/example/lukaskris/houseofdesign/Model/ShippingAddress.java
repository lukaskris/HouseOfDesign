package com.example.lukaskris.houseofdesign.Model;

/**
 * Created by Lukaskris on 30/08/2017.
 */

public class ShippingAddress {
    String name;
    String address;
    String province;
    String id_province;
    String city;
    String id_city;
    String postal_code;
    String phone;
    String status;
    int user_id;

    public ShippingAddress(String name, String address, String province, String id_province, String city, String id_city, String postal_code, String phone, String status, int user_id) {
        this.name = name;
        this.address = address;
        this.province = province;
        this.id_province = id_province;
        this.city = city;
        this.id_city = id_city;
        this.postal_code = postal_code;
        this.phone = phone;
        this.status = status;
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getProvince() {
        return province;
    }

    public String getId_province() {
        return id_province;
    }

    public String getCity() {
        return city;
    }

    public String getId_city() {
        return id_city;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public String getPhone() {
        return phone;
    }

    public String getStatus() {
        return status;
    }

    public int getUser_id() {
        return user_id;
    }
}
