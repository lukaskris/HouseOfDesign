package com.leaksoft.app.houseofdesign.model;

/**
 * Created by Lukaskris on 08/09/2017.
 */

public class Subdistrict {
    String id;
    String name;

    public Subdistrict(String id, String name) {
        this.id=id;
        this.name = name;
    }

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
}
