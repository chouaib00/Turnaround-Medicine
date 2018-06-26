package com.example.user.dipl1.entity;

public class InviteEntity {

    private int inviteId;
    private UserEntity inviteFrom;
    private UserEntity inviteTo;

    public InviteEntity() {
    }

    public int getInviteId() {
        return inviteId;
    }

    public void setInviteId(int inviteId) {
        this.inviteId = inviteId;
    }

    public UserEntity getInviteFrom() {
        return inviteFrom;
    }

    public void setInviteFrom(UserEntity inviteFrom) {
        this.inviteFrom = inviteFrom;
    }

    public UserEntity getInviteTo() {
        return inviteTo;
    }

    public void setInviteTo(UserEntity inviteTo) {
        this.inviteTo = inviteTo;
    }
}
