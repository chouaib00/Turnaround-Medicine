package com.dwork.rest.server.controller;

import com.dwork.rest.server.entity.*;
import com.dwork.rest.server.service.MedicalService;
import com.dwork.rest.server.utils.Mode;
import com.dwork.rest.server.utils.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Optional;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @Autowired
    private MedicalService service;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @ResponseBody
    public List<UserEntity> getAllUsers(){
        return service.getAllUsers();
    }

    @RequestMapping(value = "/users/{user_id}", method = RequestMethod.GET)
    @ResponseBody
    public UserEntity getUser(@PathVariable int user_id){
        return service.getUserById(user_id);
    }

    @RequestMapping(value = "/users/add", method = RequestMethod.POST)
    @ResponseBody
    public UserEntity saveUser(@RequestBody UserEntity user){
        return service.addUser(user);
    }

    @RequestMapping(value = "/users", method = RequestMethod.PUT)
    @ResponseBody
    public UserEntity updateUser(@RequestBody UserEntity user){
        return service.updateUser(user);
    }

    @RequestMapping(value = "/posts", method = RequestMethod.POST)
    @ResponseBody
    public FullPostEntity savePost(@RequestBody FullPostEntity post){
        return service.addNewPost(post);
    }

    @RequestMapping(value = "/tag", method = RequestMethod.POST)
    @ResponseBody
    public int saveTag(@RequestBody PostTagsEntity tag){
        return service.addTag(tag);
    }

    @RequestMapping(value = "/posts/{post_id}", method = RequestMethod.GET)
    @ResponseBody
    public FullPostEntity getPosts(@PathVariable int post_id){
        return service.getPostById(post_id);
    }

    @RequestMapping(value = "/posts/count/{type}", method = RequestMethod.GET)
    @ResponseBody
    public int getPostsCount(@PathVariable String type){
        return service.getPostsCount(type);
    }

    @RequestMapping(value = "/posts/my/{user_param}", method = RequestMethod.GET)
    @ResponseBody
    public List<FullPostEntity> getPostsByUser(@PathVariable int user_param){
        return service.getPostsByUser(user_param);
    }

    @RequestMapping(value = "/posts/my/{user_param}/{date_param}", method = RequestMethod.GET)
    @ResponseBody
    public List<FullPostEntity> getPostsByUserAndDate(@PathVariable int user_param, @PathVariable int date_param){
        return service.getPostsByUserAndPost_date(user_param, date_param);
    }

    @RequestMapping(value = "/post/by/{post_param}", method = RequestMethod.GET)
    @ResponseBody
    public FullPostEntity getPostByPostId(@PathVariable int post_param){
        return service.getFullPostByPostId(post_param);
    }

    @RequestMapping(value = "/tags/by/{post_param}", method = RequestMethod.GET)
    @ResponseBody
    public List<PostTagsEntity> getTagsByPostId(@PathVariable int post_param){
        return service.getPostTagsByPostId(post_param);
    }

    @RequestMapping(value = "/comments/by/{post_param}", method = RequestMethod.GET)
    @ResponseBody
    public List<PostCommentsEntity> getCommentsByPostId(@PathVariable int post_param){
        return service.getPostCommentsByPostId(post_param);
    }

    @RequestMapping(value = "/comments/count/{post_param}", method = RequestMethod.GET)
    @ResponseBody
    public int getCommentsCount(@PathVariable int post_param){
        return service.getCommentsCount(post_param);
    }

    @RequestMapping(value = "/tags/posts/{tag_param}", method = RequestMethod.GET)
    @ResponseBody
    public List<Integer> getPostIdsByTag(@PathVariable String tag_param){
        return service.findPostIdsByTag(tag_param);
    }

    @RequestMapping(value = "/posts/desc/{descParam}/{modeParam}", method = RequestMethod.GET)
    @ResponseBody
    public List<Integer> getPostIdsByDescription(@PathVariable String descParam,
                                                 @PathVariable Mode modeParam){
        return service.findPostIdsByDescription(descParam, modeParam);
    }

    @RequestMapping(value = "/add/comment", method = RequestMethod.POST)
    @ResponseBody
    public int saveComment(@RequestBody PostCommentsEntity comment){
        return service.addNewComment(comment);
    }

    @RequestMapping(value = "/add/comment/response/{postId}", method = RequestMethod.POST)
    @ResponseBody
    public int saveCommentResponse(@RequestBody PostCommentsEntity comment,
                                   @PathVariable int postId){
        return service.addNewCommentResponse(comment, postId);
    }

    @RequestMapping(value = "/notifications/unread/{uId}", method = RequestMethod.GET)
    @ResponseBody
    public int getUnreadNotificationsCount(@PathVariable int uId){
        return service.getUnreadNotificationsCount(uId);
    }

    @RequestMapping(value = "/notifications/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public List<NotificationsEntity> getAllNotificationsByUserId(@PathVariable int userId){
        return service.getAllNotificationsByUserId(userId);
    }

    @RequestMapping(value = "/notifications/reading/{nId}", method = RequestMethod.PUT)
    @ResponseBody
    public int readNotification(@PathVariable int nId){
        return service.readNotification(nId);
    }

    @RequestMapping(value = "/users/exists", method = RequestMethod.GET, params = "email")
    @ResponseBody
    public boolean emailIsExists(@Param("email") String email){
        return service.emailIsExists(email);
    }

    @RequestMapping(value = "/users/fb/{facebookId}", method = RequestMethod.GET)
    @ResponseBody
    public UserEntity getUserByFacebookId(@PathVariable String facebookId){
        return service.getUserByFacebookId(facebookId);
    }

    @RequestMapping(value = "/users/ggl/{googleId}", method = RequestMethod.GET)
    @ResponseBody
    public UserEntity getUserByGoogleId(@PathVariable String googleId){
        return service.getUserByGoogleId(googleId);
    }

    @RequestMapping(value = "/users/ta", method = RequestMethod.GET, params = "email")
    @ResponseBody
    public UserEntity getUserByEmail(@Param("email") String email){
        return service.getUserByEmail(email);
    }

    @RequestMapping(value = "/params/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public Dictionary<String, Integer> getProfileParams(@PathVariable int userId){
        return service.getProfileParams(userId);
    }

    @RequestMapping(value = "/postids/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public List<Integer> getIdsByUser(@PathVariable int userId){
        return service.getIdsByUser(userId);
    }

    @RequestMapping(value = "/followers/{followOn}", method = RequestMethod.GET)
    @ResponseBody
    public List<FollowingEntity> getAllFollowersByFollowOn(@PathVariable int followOn){
        return service.findAllFollowersByFollowOn(followOn);
    }

    @RequestMapping(value = "/followers/follow", method = RequestMethod.POST)
    @ResponseBody
    public FollowingEntity followOn(@RequestBody FollowingEntity followingEntity){
        return service.followOn(followingEntity);
    }

    @RequestMapping(value = "/followers/unfollow/{userId}/{curUserId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void unfollowOn(@PathVariable int userId, @PathVariable int curUserId){
        service.unfollowOn(userId, curUserId);
    }

    @RequestMapping(value = "/favorites/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public List<PostFavoritesEntity> getAllFavoritesByUser(@PathVariable int userId){
        return service.findFavoritesBuUser(userId);
    }

    @RequestMapping(value = "/favorites/add", method = RequestMethod.POST)
    @ResponseBody
    public PostFavoritesEntity addFavorite(@RequestBody PostFavoritesEntity postFavoritesEntity){
        return service.addNewFavorite(postFavoritesEntity);
    }

    @RequestMapping(value = "/favorites/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteFavorite(@PathVariable int id){
        service.deleteFavorite(id);
    }

    @RequestMapping(value = "/invites/add", method = RequestMethod.POST)
    @ResponseBody
    public InviteEntity addInvite(@RequestBody InviteEntity inviteEntity){
        return service.addInvite(inviteEntity);
    }

    @RequestMapping(value = "/invites/from/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public List<InviteEntity> findAllByInviteFrom(@PathVariable int userId){
        return service.findAllByInviteFrom(userId);
    }

    @RequestMapping(value = "/invites/to/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public List<InviteEntity> findAllByInviteTo(@PathVariable int userId){
        return service.findAllByInviteTo(userId);
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    @ResponseBody
    public List<Integer> getSocialInfo(){
        return service.getSocialInfo();
    }

    @RequestMapping(value = "/admin/{postId}", method = RequestMethod.GET)
    @ResponseBody
    public Post getPostToAdmin(@PathVariable int postId){
        return service.getPostToAdmin(postId);
    }

    @RequestMapping(value = "/post/blocking/{postId}/{value}", method = RequestMethod.PUT)
    public Integer blockFullPost(@PathVariable("postId") int postId, @PathVariable("value") boolean value){
        return service.blockFullPost(postId, value);
    }

    @RequestMapping(value = "/tags/distinct", method = RequestMethod.GET)
    @ResponseBody
    public List<PostTagsEntity> getDistinctTags(){
        return service.findDisctinctTags();
    }

    @RequestMapping(value = "/post/del/{id}", method = RequestMethod.GET)
    @ResponseBody
    public int deletePostById(@PathVariable("id") int id){
        service.deletePostById(id);
        return 1;
    }
}
