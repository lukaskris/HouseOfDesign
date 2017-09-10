package com.example.lukaskris.houseofdesign.Model;

import java.util.Date;

/**
 * Created by Lukaskris on 10/09/2017.
 */

public class Orders {
    int id;
    String invoice;
    int total;
    String email;
    int status;
    Date expired_at;

    public Orders(String invoice, int total, String email, int status) {
        this.invoice = invoice;
        this.total = total;
        this.email = email;
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setExpired_at(Date expired_at) {
        this.expired_at = expired_at;
    }
}
