package com.example.user.dipl1.messaging;

import java.io.Serializable;

public class User implements Serializable {
    private int userId;
    private String userName;

    public User() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
