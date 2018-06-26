package com.example.user.dipl1.entity;

public class PostFavoritesEntity {

    private int favorit_id;
    private FullPostEntity post_id;
    private UserEntity userId;

    public PostFavoritesEntity(){}

    public int getFavorit_id() {
        return favorit_id;
    }

    public void setFavorit_id(int favorit_id) {
        this.favorit_id = favorit_id;
    }

    public FullPostEntity getPost_id() {
        return post_id;
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
