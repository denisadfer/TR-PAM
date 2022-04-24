package com.dnx.trpam;

public class NftPost {

    public String img, token, owner, title, desc, creator;
    public double price;

    public NftPost(){ }

    public NftPost(String img, String token, String owner, String title, String desc, String creator, double price) {
        this.img = img;
        this.token = token;
        this.owner = owner;
        this.title = title;
        this.price = price;
        this.desc = desc;
        this.creator = creator;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) { this.desc = desc; }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) { this.creator = creator; }

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
