package com.example.user.dipl1.entity;

public class PostTagsEntity {

    private int tag_id;

    private FullPostEntity post_id;

    private String tag_value;

    public PostTagsEntity() {
    }

    public int getTag_id() {
        return tag_id;
    }

    public FullPostEntity getPost_id() {
        return post_id;
    }

    public String getTag_value() {
        return tag_value;
    }

    public void setTag_id(int tag_id) {
        this.tag_id = tag_id;
    }

    public void setPost_id(FullPostEntity post_id) {
        this.post_id = post_id;
    }

    public void setTag_value(String tag_value) {
        this.tag_value = tag_value;
    }
}
