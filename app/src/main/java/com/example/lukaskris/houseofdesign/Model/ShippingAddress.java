package com.example.lukaskris.houseofdesign.Model;

/**
 * Created by Lukaskris on 30/08/2017.
 */

public class ShippingAddress {
    String id;
    String name;
    String address;
    String province;
    String province_id;
    String city;
    String city_id;
    String subdistrict;
    String subdistrict_id;
    String postal_code;
    String phone;
    String status;
    String email;

    public ShippingAddress(String name, String address, String province, String province_id, String city, String city_id, String subdistrict, String subdistrict_id, String postal_code, String phone, String status, String email) {
        this.name = name;
        this.address = address;
        this.province = province;
        this.province_id = province_id;
        this.city = city;
        this.city_id = city_id;
        this.subdistrict = subdistrict;
        this.subdistrict_id = subdistrict_id;
        this.postal_code = postal_code;
        this.phone = phone;
        this.status = status;
        this.email = email;
    }

    public ShippingAddress(String id, String name, String address, String province, String province_id, String city, String city_id, String subdistrict, String subdistrict_id, String postal_code, String phone, String status, String email) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.province = province;
        this.province_id = province_id;
        this.city = city;
        this.city_id = city_id;
        this.subdistrict = subdistrict;
        this.subdistrict_id = subdistrict_id;
        this.postal_code = postal_code;
        this.phone = phone;
        this.status = status;
        this.email = email;
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

    public String getProvince_id() {
        return province_id;
    }

    public String getCity() {
        return city;
    }

    public String getCity_id() {
        return city_id;
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

    public String getEmail() {
        return email;
    }

    public String getSubdistrict() {
        return subdistrict;
    }

    public String getSubdistrict_id() {
        return subdistrict_id;
    }

    public String getId() {
        return id;
    }


}
