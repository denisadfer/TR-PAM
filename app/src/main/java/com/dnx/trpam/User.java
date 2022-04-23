package com.dnx.trpam;

public class User {

    public String name, email, phone, username;

    public User (){

    }

    public User(String name, String email, String phone, String username) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
