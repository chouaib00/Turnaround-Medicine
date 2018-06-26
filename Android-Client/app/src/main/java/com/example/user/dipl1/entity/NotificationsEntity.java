package com.example.user.dipl1.entity;

import java.util.Date;

public class NotificationsEntity {

    private int notificationId;
    private FullPostEntity postId;
    private UserEntity userFromId;
    private int notificationRead;
    private java.util.Date notificationDate;

    public NotificationsEntity() {}

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public FullPostEntity getPostId() {
        return postId;
    }

    public void setPostId(FullPostEntity postId) {
        this.postId = postId;
    }

    public UserEntity getUserFromId() {
        return userFromId;
    }

    public void setUserFromId(UserEntity userFromId) {
        this.userFromId = userFromId;
    }

    public int getNotificationRead() {
        return notificationRead;
    }

    public void setNotificationRead(int notificationRead) {
        this.notificationRead = notificationRead;
    }

    public Date getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(Date notificationDate) {
        this.notificationDate = notificationDate;
    }
}
