package com.leaksoft.app.houseofdesign.model;

import java.io.Serializable;

/**
 * Created by Lukaskris on 21/09/2017.
 */

public class OrdersBukti implements Serializable {
    String invoice;
    String url;
    int id;

    public OrdersBukti(String invoice, String url) {
        this.invoice = invoice;
        this.url = url;
    }

    public String getInvoice() {
        return invoice;
    }

    public String getUrl() {
        return url;
    }

    public int getId() {
        return id;
    }
}
