package com.dwork.rest.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post_comments")
public class PostCommentsEntity {

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    @Column(name = "comment_id")
    private int comment_id;

    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "comment_author")
    //@Column(name = "comment_author")
    private UserEntity user_id;

    //@ManyToOne(targetEntity = FullPostEntity.class)
    //@JoinColumn(name = "post_id")
    @Column(name = "post_id")
    private int post_id;

    @Column(name = "comment_date", columnDefinition = "DATETIME")
    private Date comment_date;

    @Column(name = "comment_value", columnDefinition = "TEXT")
    private String comment_value;

    @JsonIgnore
    @ManyToOne(targetEntity = PostCommentsEntity.class)
    @JoinColumn(name="childs")
    private PostCommentsEntity childs;

    @OneToMany(mappedBy="childs", cascade = {CascadeType.ALL},  fetch = FetchType.EAGER)
    private List<PostCommentsEntity> childsList = new ArrayList<>();

    public PostCommentsEntity() {
    }

    public int getComment_id() {
        return comment_id;
    }

    public UserEntity getUser_id() {
        return user_id;
    }

    public int getPost_id() {
        return post_id;
    }

    public Date getComment_date() {
        return comment_date;
    }

    public String getComment_value() {
        return comment_value;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public void setComment_author(UserEntity user_id) {
        this.user_id = user_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public void setComment_date(Date comment_date) {
        this.comment_date = comment_date;
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

    public List<PostCommentsEntity> getChildsList() {
        return childsList;
    }

    public void setChildsList(List<PostCommentsEntity> childsList) {
        this.childsList = childsList;
    }
}

