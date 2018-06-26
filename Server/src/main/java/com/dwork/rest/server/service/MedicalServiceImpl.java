package com.dwork.rest.server.service;

import com.dwork.rest.server.entity.*;
import com.dwork.rest.server.repository.*;
import com.dwork.rest.server.utils.Mode;
import com.dwork.rest.server.utils.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Service
public class MedicalServiceImpl implements MedicalService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FullPostRepository fullPostRepository;

    @Autowired
    private PostTagsRepository postTagsRepository;

    @Autowired
    private PostCommentsRepository postCommentsRepository;

    @Autowired
    private NotificationsRepository notificationsRepository;

    @Autowired
    private FollowingEntityRepository followingEntityRepository;

    @Autowired
    private PostFavoritesRepository postFavoritesRepository;

    @Autowired
    private InviteRepository inviteRepository;

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public UserEntity getUserById(int id) {
        return userRepository.findOne(id);
    }

    public UserEntity addUser(UserEntity user) {
        return userRepository.saveAndFlush(user);
    }

    public UserEntity updateUser(UserEntity user) {
        return userRepository.saveAndFlush(user);
    }

    public FullPostEntity addNewPost(FullPostEntity fullPostEntity) {
        return fullPostRepository.save(fullPostEntity);
    }

    public int addTag(PostTagsEntity tag) {
        try{
            postTagsRepository.saveAndFlush(tag);
            return 200;
        }catch (Exception e){
            return 500;
        }
    }

    public FullPostEntity getPostById(int post_id) {
        if(fullPostRepository.exists(post_id)){
            return fullPostRepository.findOne(post_id);
        }else{
            return null;
        }
    }

    public int getPostsCount(String type) {
        if(type.equals("max")) {
            return fullPostRepository.findMax();
        }else{
            return fullPostRepository.findMin();
        }
    }

    public List<FullPostEntity> getPostsByUser(int user_id) {
        return fullPostRepository.findPostsByUser_id(user_id);
    }

    public List<FullPostEntity> getPostsByUserAndPost_date(int user_id, int post_date) {
        return fullPostRepository.findPostsByUser_idAndPost_date(user_id, post_date);
    }

    public List<PostTagsEntity> getPostTagsByPostId(int post_param) {
        return postTagsRepository.findPostTagsEntitiesByPost_id(post_param);
    }

    public List<PostCommentsEntity> getPostCommentsByPostId(int post_param) {
        List<PostCommentsEntity> list = StreamSupport.stream(postCommentsRepository.findAll().spliterator(), false)
                .filter(p -> p.getPost_id() == post_param && p.getChilds() == null).collect(Collectors.toList());

        System.out.println(list.size());

        return list;
        //return postCommentsRepository.findPostCommentsEntitiesByPost_id(post_param);
    }

    public FullPostEntity getFullPostByPostId(int post_param) {
        return fullPostRepository.findFullPostEntityByPost_id(post_param);
    }

    public int getCommentsCount(int post_param) {
        return postCommentsRepository.getCountComments(post_param);
    }

    public List<Integer> findPostIdsByTag(String tag_param) {
        return postTagsRepository.findPostIdsByTag_value(tag_param);
    }

    @Override
    public List<Integer> findPostIdsByDescription(String descParam, Mode mode) {
        List<Integer> retList = null;

        if (mode.equals(Mode.PRIVATE) ){
            retList = fullPostRepository.getIdsByDescription(descParam);
        }else if(mode.equals(Mode.PUBLIC)){
            retList = fullPostRepository.getIdsByDescriptionWithSecurity(descParam);
        }

        return retList;
    }

    @Override
    public int addNewComment(PostCommentsEntity comment) {
        try{
            postCommentsRepository.saveAndFlush(comment);
            return 200;
        } catch (Exception e){
            return 500;
        }
    }

    @Override
    public int addNewCommentResponse(PostCommentsEntity response, int commentId) {
        try{
            PostCommentsEntity temp = postCommentsRepository.findOne(commentId);
            response.setChilds(temp);
            postCommentsRepository.saveAndFlush(response);
            return 200;
        } catch (Exception e){
            return 500;
        }
    }

    @Override
    public int getUnreadNotificationsCount(int userId) {
        return notificationsRepository.getUnread(userId);
    }

    @Override
    public List<NotificationsEntity> getAllNotificationsByUserId(int userId) {
        return notificationsRepository.getAllByUser(userId);
    }

    @Override
    public int readNotification(int notificationId) {
        try {
            NotificationsEntity notification =
                    notificationsRepository.findOne(notificationId);

            notification.setNotificationRead(1);

            notificationsRepository.saveAndFlush(notification);
            return 200;
        }catch (Exception e){
            return 500;
        }
    }

    @Override
    public boolean emailIsExists(String email) {
        return userRepository.emailIsExists(email);
    }

    @Override
    public UserEntity getUserByFacebookId(String facebookId) {
        return userRepository.getUserEntityByFacebook_id(facebookId);
    }

    @Override
    public UserEntity getUserByGoogleId(String googleId) {
        return userRepository.getUserEntityByGoogle_id(googleId);
    }

    @Override
    public UserEntity getUserByEmail(String email) {
        return userRepository.getUserEntityByUser_email(email);
    }

    @Override
    public Dictionary<String, Integer> getProfileParams(int userId) {
        Dictionary<String , Integer> params = new Hashtable<>();
        params.put("comments", postCommentsRepository.getCountCommentsByUser_id(userId));
        params.put("posts", fullPostRepository.getPostsCountByUser_id(userId));
        params.put("followers", followingEntityRepository.findAllByFollowOn(userId).size());
        return params;
    }

    @Override
    public List<Integer> getIdsByUser(int userID) {
        return fullPostRepository.getIdsByUser(userID);
    }

    @Override
    public List<FollowingEntity> findAllFollowersByFollowOn(int followOn) {
        return followingEntityRepository.findAllByFollowOn(followOn);
    }

    @Override
    public FollowingEntity followOn(FollowingEntity followingEntity) {
        return followingEntityRepository.saveAndFlush(followingEntity);
    }

    @Override
    public void unfollowOn(int userId, int curUserId) {
        try {
            followingEntityRepository.unfollowOnUser(userId, curUserId);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public List<PostFavoritesEntity> findFavoritesBuUser(int userId) {
        return postFavoritesRepository.findAllByUserId(userId);
    }

    @Override
    public PostFavoritesEntity addNewFavorite(PostFavoritesEntity postFavoritesEntity) {
        if (postFavoritesRepository.condition(postFavoritesEntity.getUserId().getUser_id(),
                postFavoritesEntity.getPost_id().getPost_id()).size() == 0) {
            return postFavoritesRepository.saveAndFlush(postFavoritesEntity);
        }else if(postFavoritesRepository.condition(postFavoritesEntity.getUserId().getUser_id(),
                postFavoritesEntity.getPost_id().getPost_id()).size() > 0){
            return null;
        }else{
            return null;
        }
    }

    @Override
    public void deleteFavorite(int id) {
        postFavoritesRepository.delete(id);
    }

    @Override
    public InviteEntity addInvite(InviteEntity inviteEntity) {

        if (inviteRepository.exists(inviteEntity.getInviteFrom().getUser_id(), inviteEntity.getInviteTo().getUser_id()) == 0){
            return inviteRepository.saveAndFlush(inviteEntity);
        }else{
            return new InviteEntity();
        }

    }

    @Override
    public List<InviteEntity> findAllByInviteTo(int userId) {
        return inviteRepository.findAllByInviteTo(userId);
    }

    @Override
    public List<InviteEntity> findAllByInviteFrom(int userId) {
        return inviteRepository.findAllByInviteFrom(userId);
    }

    @Override
    public List<Integer> getSocialInfo() {
        List<Integer> list = new ArrayList<>();
        list.add(userRepository.getUserCount());
        list.add(userRepository.getDoctorCount());
        list.add(fullPostRepository.getUserPostsCount());
        list.add(fullPostRepository.getDoctorPostsCount());
        list.add(postTagsRepository.findAll().size());
        list.add(postCommentsRepository.findAll().size());
        return list;
    }

    @Override
    public Post getPostToAdmin(int postId) {
        Post post = new Post();
        FullPostEntity postEntity = fullPostRepository.findOne(postId);
        post.Id = postEntity.getPost_id();
        post.UserName = postEntity.getUser_id().getUser_name();
        post.Description = postEntity.getDescription();
        post.Photo = postEntity.getPhoto();
        return post;
    }

    @Override
    public Integer blockFullPost(int postId, boolean value) {
        FullPostEntity post = fullPostRepository.findOne(postId);
        post.setBlocking(value);
        fullPostRepository.saveAndFlush(post);
        return post.getPost_id();
    }

    @Override
    public List<PostTagsEntity> findDisctinctTags() {
        return postTagsRepository.findDistinctTags();
    }

    @Override
    public void deletePostById(int id) {
        fullPostRepository.delete(id);
        postCommentsRepository.deleteCommentsByPost_id(id);
    }

}
