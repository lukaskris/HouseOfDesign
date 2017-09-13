package com.example.lukaskris.houseofdesign.Model;

/**
 * Created by lukaskris on 9/11/2017.
 */

public class OrdersDetail {
    String invoice;
    int id;
    int item_id;
    int sub_item_id;
    int subtotal;
    int price;
    int quantity;
    String name;
    String thumbnail;

    public OrdersDetail(String invoice, int item_id, int sub_item_id, int subtotal, int price,int quantity) {
        this.invoice = invoice;
        this.item_id = item_id;
        this.sub_item_id = sub_item_id;
        this.subtotal = subtotal;
        this.price = price;
        this.quantity = quantity;
    }

    public OrdersDetail(String invoice, int item_id, int sub_item_id, int subtotal, int price, int quantity, String name, String thumbnail) {
        this.invoice = invoice;
        this.item_id = item_id;
        this.sub_item_id = sub_item_id;
        this.subtotal = subtotal;
        this.price = price;
        this.quantity = quantity;
        this.name = name;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getInvoice() {
        return invoice;
    }

    public int getId() {
        return id;
    }

    public int getItem_id() {
        return item_id;
    }

    public int getSub_item_id() {
        return sub_item_id;
    }

    public int getSubtotal() {
        return subtotal;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}
