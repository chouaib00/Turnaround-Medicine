package com.example.user.dipl1.realm;

import io.realm.RealmObject;

public class Notification extends RealmObject {

    private int notificationId;
    private int postId;
    private byte[] postIcon;
    private String userFLF;
    private int notificationRead;
    private String notificationDate;

    public Notification() {}

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getUserFLF() {
        return userFLF;
    }

    public void setUserFLF(String userFLF) {
        this.userFLF = userFLF;
    }

    public int getNotificationRead() {
        return notificationRead;
    }

    public void setNotificationRead(int notificationRead) {
        this.notificationRead = notificationRead;
    }

    public String getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(String notificationDate) {
        this.notificationDate = notificationDate;
    }

    public byte[] getPostIcon() {
        return postIcon;
    }

    public void setPostIcon(byte[] postIcon) {
        this.postIcon = postIcon;
    }
}
