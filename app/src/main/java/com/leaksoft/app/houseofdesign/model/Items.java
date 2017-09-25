package com.leaksoft.app.houseofdesign.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by xub on 28/08/17.
 */


public class Items implements Serializable {
    private String name;
    private int price;
    private String description;
    private int category_id;

    private String images;
    private int weight;
    private int id;
    private String thumbnail;

    public Items() {}

    public Items(String name, int price, String desc, int category,int weight, String image, String thumbnail) {
        this.name = name;
        this.price = price;
        this.description = desc;
        this.category_id = category;
        this.images = image;
        this.thumbnail = thumbnail;
        this.weight = weight;
    }

    public Items(String name, int price, String desc, int category_id, String image, int id, String thumbnail) {
        this.name = name;
        this.price = price;
        this.description = desc;
        this.category_id = category_id;
        this.images = image;
        this.id = id;
        this.thumbnail = thumbnail;
    }

    public Items(String name, int price, String thumbnail) {
        this.name = name;
        this.price = price;
        this.thumbnail = thumbnail;
    }

    public int getWeight() {
        return weight;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDesc(String desc) {
        this.description = desc;
    }

    public int getCategory() {
        return category_id;
    }

    public void setCategory(int category) {
        this.category_id = category;
    }

    public List<String> getImages() {
        String temp = images.replace("[","");
        temp = temp.replace("]","");
        temp = temp.replace("\"","");
        temp = temp.replace("\\","");
        String[] image = temp.split(",");

        List<String> listTemp;
        listTemp = new ArrayList<>(Arrays.asList(image));
        return listTemp;
    }

    public void setImages(String image) {
        this.images = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}