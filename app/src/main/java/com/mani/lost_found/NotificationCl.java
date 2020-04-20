package com.mani.lost_found;

public class NotificationCl{
    String Notification;


    String userImage;
    public NotificationCl(){}

    public NotificationCl(String Notification,String userImage)
    {
        this.Notification=Notification;
        this.userImage=userImage;
    }


    public String getNotification() {
        return Notification;
    }


    public void setNotification(String Notification) {
        this.Notification = Notification;
    }
    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }


}
