package com.dwork.rest.server.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "notifications")
public class NotificationsEntity {

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    @Column(name = "notificationId")
    private int notificationId;

    @ManyToOne(targetEntity = FullPostEntity.class)
    @JoinColumn(name = "postId")
    private FullPostEntity postId;

    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "userFromId")
    private UserEntity userFromId;

    @Column(name = "notificationRead")
    private int notificationRead;

    @Column(name = "notificationDate")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date notificationDate;

    public NotificationsEntity(){}

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
