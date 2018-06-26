package com.dwork.rest.server.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "post_favorites")
public class PostFavoritesEntity {

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    @Column(name = "favorit_id")
    private int favorit_id;

    //@Column(name = "post_id")
    @ManyToOne(targetEntity = FullPostEntity.class)
    @JoinColumn(name = "post_id")
    private FullPostEntity post_id;

    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "userId")
    private UserEntity userId;

    public PostFavoritesEntity() {
    }

    public int getFavorit_id() {
        return favorit_id;
    }

    public FullPostEntity getPost_id() {
        return post_id;
    }

    public void setFavorit_id(int favorit_id) {
        this.favorit_id = favorit_id;
    }

    public void setPost_id(FullPostEntity post_id) {
        this.post_id = post_id;
    }

    public UserEntity getUserId() {
        return userId;
    }

    public void setUserId(UserEntity userId) {
        this.userId = userId;
    }
}
