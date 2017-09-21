package com.leaksoft.app.houseofdesign.model;

/**
 * Created by lukaskris on 9/11/2017.
 */

public class OrdersInfo {
    int id;
    String invoice;
    String kurir;
    String type;
    int price;
    int shipping_id;
    String no_resi;
    String kurir_id;

    public OrdersInfo(String invoice, String kurir_id, String kurir, String type, int price, int shipping_id) {
        this.id = id;
        this.invoice = invoice;
        this.kurir_id=kurir_id;
        this.kurir = kurir;
        this.type = type;
        this.price = price;
        this.shipping_id = shipping_id;
    }

    public String getNo_resi() {
        return no_resi;
    }

    public String getKurir_id() {
        return kurir_id;
    }

    public int getId() {
        return id;
    }

    public String getInvoice() {
        return invoice;
    }

    public String getKurir() {
        return kurir;
    }

    public String getType() {
        return type;
    }

    public int getPrice() {
        return price;
    }

    public int getShipping_id() {
        return shipping_id;
    }
}
