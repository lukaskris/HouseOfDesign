package com.example.lukaskris.houseofdesign.Model;

import java.util.Objects;

/**
 * Created by xub on 06/09/17.
 */

public class Cart {
    Items item;
    String color;
    String size;
    String subitem_id;
    int quantity;
    int quantity_max;

    public Cart(Items item, String color, String size, String subitem_id, int quantity, int quantity_max) {
        this.item = item;
        this.color = color;
        this.size = size;
        this.subitem_id = subitem_id;
        this.quantity = quantity;
        this.quantity_max = quantity_max;
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

    public String getSubitem_id() {
        return subitem_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getQuantity_max() {
        return quantity_max;
    }

}
