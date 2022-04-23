package com.dnx.trpam;

import java.util.Date;

public class History {
    public String owner,buyer, aksi;
    public String price, token;
    public long dateBuy;

    public History(String owner, String buyer, String price, String aksi, String token) {
        this.owner = owner;
        this.buyer = buyer;
        this.price = price;
        this.aksi = aksi;
        this.token = token;
        dateBuy = new Date().getTime();
    }

    public History(){

    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public long getDateBuy() {
        return dateBuy;
    }

    public void setDateBuy(long dateBuy) {
        this.dateBuy = dateBuy;
    }

    public String getAksi() {
        return aksi;
    }

    public void setAksi(String aksi) {
        this.aksi = aksi;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
