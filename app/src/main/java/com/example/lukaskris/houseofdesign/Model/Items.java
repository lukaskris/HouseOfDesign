package com.example.lukaskris.houseofdesign.Model;

import java.io.Serializable;
import java.util.List;


/**
 * Created by xub on 28/08/17.
 */


public class Items implements Serializable {
    private String name;
    private String price;
    private String desc;
    private String category_id;
    private List<String> image;
    private int id;
    private String thumbnail;

    public Items() {
    }

    public Items(String name, String price, String desc, String category, List<String> image, String thumbnail) {
        this.name = name;
        this.price = price;
        this.desc = desc;
        this.category_id = category;
        this.image = image;
        this.thumbnail = thumbnail;
    }

    public Items(String name, String price, String desc, String category_id, List<String> image, int id, String thumbnail) {
        this.name = name;
        this.price = price;
        this.desc = desc;
        this.category_id = category_id;
        this.image = image;
        this.id = id;
        this.thumbnail = thumbnail;
    }

    public String getName() {

        return name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCategory() {
        return category_id;
    }

    public void setCategory(String category) {
        this.category_id = category;
    }

    public List<String> getImage() {
        return image;
    }

    public void setImage(List<String> image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
