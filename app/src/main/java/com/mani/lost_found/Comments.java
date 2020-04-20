package com.mani.lost_found;
import java.util.Date;

public class Comments {

    private String message;
    private String user_id;
    private String postId;
    private long timestamp;
    public String commentId;
    public Comments(){

    }

    public Comments(String message, String user_id,String postId, long timestamp) {
        this.message = message;
        this.user_id = user_id;
        this.postId=postId;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPostId() {
        return postId;
    }
    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Comments withID(String commentId) {
      this.commentId=commentId;
      return this;
    }
}
