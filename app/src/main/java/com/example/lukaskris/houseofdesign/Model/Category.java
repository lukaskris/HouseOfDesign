package com.example.lukaskris.houseofdesign.Model;

import java.io.Serializable;

/**
 * Created by xub on 29/08/17.
 */

public class Category implements Serializable {
    int id;
    String name;

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
