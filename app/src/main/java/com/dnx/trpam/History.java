package com.dnx.trpam;

import java.util.Date;

public class History {
    public String aksi;
    public String token;
    public long dateBuy;

    public History(String aksi, String token) {
        this.aksi = aksi;
        this.token = token;
        dateBuy = new Date().getTime();
    }

    public History(){

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
