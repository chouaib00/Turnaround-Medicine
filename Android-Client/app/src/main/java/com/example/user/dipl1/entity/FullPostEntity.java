package com.example.user.dipl1.entity;

public class FullPostEntity {
    private int post_id;

    private UserEntity user_id;

    private String description;

    private byte[] photo;

    private java.util.Date post_date;

    private String post_status;

    private boolean blocking;

    public FullPostEntity() {
    }

    public int getPost_id() {
        return post_id;
    }

    public UserEntity getUser_id() {
        return user_id;
    }

    public String getDescription() {
        return description;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public java.util.Date getPost_date() {
        return post_date;
    }

    public String getPost_status() {
        return post_status;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public void setUser_id(UserEntity user_id) {
        this.user_id = user_id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public void setPost_date(java.util.Date post_date) {
        this.post_date = post_date;
    }

    public void setPost_status(String post_status) {
        this.post_status = post_status;
    }

    public boolean isBlocking() {
        return blocking;
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }
}
