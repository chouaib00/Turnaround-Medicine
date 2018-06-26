package com.dwork.rest.server.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "user_follows")
public class FollowingEntity {

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    @Column(name = "uuid")
    private int uuid;

    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "userId")
    private UserEntity userId;

    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "followOn")
    private UserEntity followOn;

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
