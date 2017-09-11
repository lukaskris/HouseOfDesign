package com.example.lukaskris.houseofdesign.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Lukaskris on 10/09/2017.
 */

public class Orders implements Serializable{
    int id;
    String invoice;
    int total;
    String email;
    int status;
    Date expired_at;

    public Orders(int total, String email, int status) {
        this.total = total;
        this.email = email;
        this.status = status;
    }

    public Orders(int id, String invoice, int total, String email, int status, Date expired_at) {
        this.id = id;
        this.invoice = invoice;
        this.total = total;
        this.email = email;
        this.status = status;
        this.expired_at = expired_at;
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

    public int getId() {
        return id;
    }

    public String getInvoice() {
        return invoice;
    }

    public int getTotal() {
        return total;
    }

    public String getEmail() {
        return email;
    }

    public int getStatus() {
        return status;
    }

    public Date getExpired_at() {
        return expired_at;
    }
}
