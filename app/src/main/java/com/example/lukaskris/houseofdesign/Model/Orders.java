package com.example.lukaskris.houseofdesign.Model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    String name;
    String thumbnail;
    String price;
    String quantity;
    List<OrdersDetail> detail;

    Orders(){
        detail = new ArrayList<>();
    }

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

    public Date getExpired_at() {
        return expired_at;
    }

    public List<OrdersDetail> getDetail() {
        return detail;
    }

    public void setDetail(List<OrdersDetail> detail) {
        this.detail = detail;
    }

    public String getName() {
        return name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getPrice() {
        return price;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getStatus(){
        String statuscode = "";
        if(status == 0){
            statuscode = "MENUNGGU PEMBAYARAN";
        }else if(status == 1){
            statuscode = "PEMBAYARAN TERVERIFIKASI";
        }else if(status == 2){
            statuscode = "PROSES";
        }else if(status == 3){
            statuscode = "BARANG TELAH DIKIRIM";
        }else if(status == 4){
            statuscode = "BARANG DITERIMA";
        }else if(status == 5){
            statuscode = "KADALUARSA";
        }
        return statuscode;
    }

    public String getStatusDetail(){
        String statuscode = "";
        if(status == 1){
            statuscode = "Menunggu barang untuk diproses";
        }else if(status == 2){
            statuscode = "Barang telah diproses dan siap dikirimkan";
        }else if(status == 3){
            statuscode = "Barang sedang berada dalam pengiriman";
        }
        return statuscode;
    }

    public int getStatusCode(){
        return status;
    }

    public String getExpired(){
        String expired = "batas pembayaran ";
        expired += new SimpleDateFormat("dd MMMM yyyy HH:mm").format(expired_at);

        return expired;
    }
}
