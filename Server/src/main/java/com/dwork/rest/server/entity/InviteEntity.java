package com.dwork.rest.server.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "invite")
public class InviteEntity {

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    @Column(name = "inviteId")
    private int inviteId;

    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "inviteFrom")
    private UserEntity inviteFrom;

    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "inviteTo")
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
