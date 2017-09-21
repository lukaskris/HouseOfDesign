package com.leaksoft.app.houseofdesign.model;

/**
 * Created by xub on 29/08/17.
 */

public class SubItem {
    int id;
    int item_id;
    String size;
    String color;
    int quantity;

    public SubItem(int id, int item_id, String size, String color, int quantity) {
        this.id = id;
        this.item_id = item_id;
        this.size = size;
        this.color = color;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
