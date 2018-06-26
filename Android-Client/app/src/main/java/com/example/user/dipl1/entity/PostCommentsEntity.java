package com.example.user.dipl1.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostCommentsEntity {

    private int comment_id;
    private UserEntity user_id;
    private int post_id;
    private Date comment_date;
    private String comment_value;
    private PostCommentsEntity childs;
    private ArrayList<PostCommentsEntity> childsList;

    public PostCommentsEntity() {
        childsList = new ArrayList<>();
    }


    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }


    public UserEntity getUser_id() {
        return user_id;
    }

    public void setUser_id(UserEntity user_id) {
        this.user_id = user_id;
    }


    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }


    public Date getComment_date() {
        return comment_date;
    }

    public void setComment_date(Date comment_date) {
        this.comment_date = comment_date;
    }


    public String getComment_value() {
        return comment_value;
    }

    public void setComment_value(String comment_value) {
        this.comment_value = comment_value;
    }


    public PostCommentsEntity getChilds() {
        return childs;
    }

    public void setChilds(PostCommentsEntity childs) {
        this.childs = childs;
    }


    public ArrayList<PostCommentsEntity> getChildsList() {
        return childsList;
    }

    public void setChildsList(ArrayList<PostCommentsEntity> childsList) {
        this.childsList = childsList;
    }

    public void addChild(PostCommentsEntity postCommentsEntity){
        childsList.add(postCommentsEntity);
    }
}
