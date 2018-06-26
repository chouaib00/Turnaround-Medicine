package com.dwork.rest.server.service;

import com.dwork.rest.server.entity.*;
import com.dwork.rest.server.utils.Mode;
import com.dwork.rest.server.utils.Post;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Optional;

public interface MedicalService {
    List<UserEntity> getAllUsers();
    UserEntity getUserById(int id);
    UserEntity addUser(UserEntity user);
    UserEntity updateUser(UserEntity user);
    FullPostEntity addNewPost(FullPostEntity postEntity);
    int addTag(PostTagsEntity tag);
    FullPostEntity getPostById(int post_id);
    int getPostsCount(String type);
    List<FullPostEntity> getPostsByUser(int user_id);
    List<FullPostEntity> getPostsByUserAndPost_date(int user_id, int post_date);

    List<PostTagsEntity> getPostTagsByPostId(int post_param);
    List<PostCommentsEntity> getPostCommentsByPostId(int post_param);
    FullPostEntity getFullPostByPostId(int post_param);

    int getCommentsCount(int post_param);
    List<Integer> findPostIdsByTag(String tag_param);
    List<Integer> findPostIdsByDescription(String descParam, Mode mode);

    int addNewComment(PostCommentsEntity comment);
    int addNewCommentResponse(PostCommentsEntity response, int commentId);

    int getUnreadNotificationsCount(int userId);
    List<NotificationsEntity> getAllNotificationsByUserId(int userId);
    int readNotification(int notificationId);

    boolean emailIsExists(String email);
    UserEntity getUserByFacebookId(String facebookId);
    UserEntity getUserByGoogleId(String googleId);
    UserEntity getUserByEmail(String email);

    Dictionary<String, Integer> getProfileParams(int userId);

    List<Integer> getIdsByUser(int userID);

    List<FollowingEntity> findAllFollowersByFollowOn(int followOn);
    FollowingEntity followOn(FollowingEntity followingEntity);
    void unfollowOn(int userId, int curUserId);

    List<PostFavoritesEntity> findFavoritesBuUser(int userId);
    PostFavoritesEntity addNewFavorite(PostFavoritesEntity postFavoritesEntity);
    void deleteFavorite(int id);

    InviteEntity addInvite(InviteEntity inviteEntity);
    List<InviteEntity> findAllByInviteTo(int userId);
    List<InviteEntity> findAllByInviteFrom(int userId);

    List<Integer> getSocialInfo();
    Post getPostToAdmin(int postId);

    Integer blockFullPost(int postId, boolean value);

    List<PostTagsEntity> findDisctinctTags();

    void deletePostById(int id);
}
