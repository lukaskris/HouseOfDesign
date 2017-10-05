package com.leaksoft.app.houseofdesign.model;

import java.util.Date;

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
    Date datebirth;
    String gender;
    String firebasetoken;

    public Customer(String name, String email, String phone, String password, String picture) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.picture = picture;
    }

    public Customer(String name, String email, String phone, String password, String picture, Date datebirth, String gender, String firebasetoken) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.picture = picture;
        this.datebirth = datebirth;
        this.gender = gender;
        this.firebasetoken = firebasetoken;
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

    public Date getDatebirth() {
        return datebirth;
    }

    public String getGender() {
        return gender;
    }

    public void setDatebirth(Date datebirth) {
        this.datebirth = datebirth;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirebasetoken() {
        return firebasetoken;
    }

    public void setFirebasetoken(String firebasetoken) {
        this.firebasetoken = firebasetoken;
    }
}
