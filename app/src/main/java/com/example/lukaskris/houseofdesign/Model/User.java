package com.example.lukaskris.houseofdesign.Model;

import android.net.Uri;

/**
 * Created by Lukaskris on 15/07/2017.
 */

public class User {
    String name;
    String email;
    String uid;
    String photo;

    public User(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
