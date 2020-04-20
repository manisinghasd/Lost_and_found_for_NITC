package com.mani.lost_found;

import android.util.Log;

import java.sql.Timestamp;

public class LostAndFoundPost {
    public String post_type,title,post_desc,user_id,image_url,image_thumb,user_name;
    public long timestamp;
    public String lostAndFoundPostId;

    public LostAndFoundPost() {}

    public LostAndFoundPost(String post_type, String title, String post_desc, String user_id,String user_name, String image_url, String image_thumb, long timestamp) {
        this.post_type = post_type;
        this.title = title;
        this.post_desc = post_desc;
        this.user_id = user_id;
        this.user_name=user_name;
        this.image_url = image_url;
        this.image_thumb = image_thumb;
        this.timestamp = timestamp;

    }

    public String getPost_type() {
        return post_type;
    }

    public void setPost_type(String post_type) {
        this.post_type = post_type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPost_desc() {
        return post_desc;
    }

    public void setPost_desc(String post_desc) {
        this.post_desc = post_desc;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUser_name() {
        return user_name;
    }
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public LostAndFoundPost withID(String key) {
        this.lostAndFoundPostId=key;
        return this;
    }
}
