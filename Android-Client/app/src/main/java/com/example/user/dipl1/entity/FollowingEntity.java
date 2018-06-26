package com.example.user.dipl1.entity;

public class FollowingEntity {

    private int uuid;
    private UserEntity userId;
    private UserEntity followOn;

    public FollowingEntity(){}

    public int getUuid() {
        return uuid;
    }

    public void setUuid(int uuid) {
        this.uuid = uuid;
    }

    public UserEntity getUserId() {
        return userId;
    }

    public void setUserId(UserEntity userId) {
        this.userId = userId;
    }

    public UserEntity getFollowOn() {
        return followOn;
    }

    public void setFollowOn(UserEntity followOn) {
        this.followOn = followOn;
    }
}
