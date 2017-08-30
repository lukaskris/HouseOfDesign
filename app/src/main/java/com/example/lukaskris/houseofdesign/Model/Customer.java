package com.example.lukaskris.houseofdesign.Model;

/**
 * Created by xub on 29/08/17.
 */

public class Customer {
    int id;
    String name;
    String email;
    String phone;
    String password;
    String picture;

    public Customer(String name, String email, String phone, String password, String picture) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.picture = picture;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public String getPicture() {
        return picture;
    }
}
