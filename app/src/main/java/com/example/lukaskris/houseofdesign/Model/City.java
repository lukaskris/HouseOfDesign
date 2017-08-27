package com.example.lukaskris.houseofdesign.Model;

/**
 * Created by Lukaskris on 12/08/2017.
 */

public class City {
    String id;
    String name;

    public City(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public City(String id, String name) {

        this.id = id;
        this.name = name;
    }
}
