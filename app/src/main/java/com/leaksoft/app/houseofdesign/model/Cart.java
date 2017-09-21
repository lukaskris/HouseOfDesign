package com.leaksoft.app.houseofdesign.model;

import java.io.Serializable;

/**
 * Created by xub on 06/09/17.
 */

public class Cart implements Serializable{
    Items item;
    String color;
    String size;
    int subitem_id;
    int quantity;
    int quantity_max;

    public Cart(Items item, String color, String size, int subitem_id, int quantity, int quantity_max) {
        this.item = item;
        this.color = color;
        this.size = size;
        this.subitem_id = subitem_id;
        this.quantity = quantity;
        this.quantity_max = quantity_max;
    }

    public Cart(Items item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setQuantity_max(int quantity_max) {
        this.quantity_max = quantity_max;
    }

    public Items getItem() {
        return item;
    }

    public String getColor() {
        return color;
    }

    public String getSize() {
        return size;
    }

    public int getSubitem_id() {
        return subitem_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getQuantity_max() {
        return quantity_max;
    }

}
