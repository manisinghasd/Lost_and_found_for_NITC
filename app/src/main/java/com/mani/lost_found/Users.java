package com.mani.lost_found;

public class Users {
    private String userName;
    private String UserEmail;
    private String userPhoto;

    public Users(){};

    public Users(String userName, String UserEmail, String userPhoto) {
        this.userName = userName;
        this.UserEmail = UserEmail;
        this.userPhoto = userPhoto;
    }



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String UserEmail) {
        this.UserEmail = UserEmail;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }
}
