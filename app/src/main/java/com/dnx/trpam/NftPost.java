package com.dnx.trpam;

public class NftPost {

    public String img, token, owner, title;
    public double price;

    public NftPost(){ }

    public NftPost(String img, String token, String owner, String title, double price) {
        this.img = img;
        this.token = token;
        this.owner = owner;
        this.title = title;
        this.price = price;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
