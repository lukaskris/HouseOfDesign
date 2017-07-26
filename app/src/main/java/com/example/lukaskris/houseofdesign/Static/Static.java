package com.example.lukaskris.houseofdesign.Static;

import com.example.lukaskris.houseofdesign.Model.User;

/**
 * Created by Lukaskris on 15/07/2017.
 */

public class Static {
    public static User user=null;

    public Static() {}

    public static User getInstance(){
        if(user == null){
            user = new User();
        }
        return user;
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        Static.user = user;
    }
}
