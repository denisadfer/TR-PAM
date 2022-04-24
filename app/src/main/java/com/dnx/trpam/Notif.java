package com.dnx.trpam;

import java.util.Date;

public class Notif {

    public String notif, userNotif, isNew;
    public long dateNotif;

    Notif(){

    }

    public Notif(String notif, String userNotif, String isNew) {
        this.notif = notif;
        this.isNew = isNew;
        this.userNotif = userNotif;
        dateNotif = new Date().getTime();
    }

    public String getNotif() {
        return notif;
    }

    public void setNotif(String notif) {
        this.notif = notif;
    }

    public long getDateNotif() {
        return dateNotif;
    }

    public void setDateNotif(long dateNotif) {
        this.dateNotif = dateNotif;
    }

    public String getUserNotif() {
        return userNotif;
    }

    public void setUserNotif(String userNotif) {
        this.userNotif = userNotif;
    }

    public String getIsNew() {
        return isNew;
    }

    public void setIsNew(String isNew) {
        this.isNew = isNew;
    }
}
