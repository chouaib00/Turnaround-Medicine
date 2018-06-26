package com.example.user.dipl1.network;

import com.example.user.dipl1.entity.FollowingEntity;
import com.example.user.dipl1.entity.FullPostEntity;
import com.example.user.dipl1.entity.InviteEntity;
import com.example.user.dipl1.entity.NotificationsEntity;
import com.example.user.dipl1.entity.PostCommentsEntity;
import com.example.user.dipl1.entity.PostFavoritesEntity;
import com.example.user.dipl1.entity.PostTagsEntity;
import com.example.user.dipl1.entity.UserEntity;
import com.example.user.dipl1.utils.Mode;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MedicalAPI {

    //@Headers("Accept-Charset: utf-8")
    @POST("posts")
    Call<FullPostEntity> addPost(@Body FullPostEntity fullPostEntity);

    //@Headers("Accept-Charset: utf-8")
    @GET("users/{user_id}")
    Call<UserEntity> getUserById(@Path("user_id") int user_id);

    //@Headers("Accept-Charset: utf-8")
    @POST("/tag")
    Call<Integer> addTag(@Body PostTagsEntity tag);

   // @Headers("Accept-Charset: utf-8")
    @GET("/posts/{post_id}")
    Call<FullPostEntity> getPostById(@Path("post_id") int post_id);

    //@Headers("Accept-Charset: utf-8")
    @GET("/posts/count/{type}")
    Call<Integer> getPostCount(@Path("type") String type);

    //@Headers("Accept-Charset: utf-8")
    @GET("/posts/my/{user_param}")
    Call<List<FullPostEntity>> getPostsByUser(@Path("user_param") int user_param);

    //@Headers("Accept-Charset: utf-8")
    @GET("/posts/my/{user_param}/{date_param}")
    Call<List<FullPostEntity>> getPostsByUserAndDate(@Path("user_param") int user_param,
                                        @Path("date_param") int date_param);

    //@Headers("Accept-Charset: utf-8")
    @GET("/post/by/{post_param}")
    Call<FullPostEntity> getFullPostById(@Path("post_param") int post_param);

    //@Headers("Accept-Charset: utf-8")
    @GET("/tags/by/{post_param}")
    Call<List<PostTagsEntity>> getFullPostTagsById(@Path("post_param") int post_param);

    //@Headers("Accept-Charset: utf-8")
    @GET("/comments/count/{post_param}")
    Call<Integer> getCommentsCount(@Path("post_param") int post_param);

    //@Headers("Accept-Charset: utf-8")
    @GET("/tags/posts/{tag_param}")
    Call<List<Integer>> getPostIdsByTag(@Path("tag_param") String tag_param);

    //@Headers("Accept-Charset: utf-8")
    @GET("/posts/desc/{descParam}/{modeParam}")
    Call<List<Integer>> getPostIdsByDescription(@Path("descParam") String descParam,
                                                @Path("modeParam")Mode modeParam);

    //@Headers("Accept-Charset: utf-8")
    @GET("/comments/by/{post_param}")
    Call<List<PostCommentsEntity>> getCommentsByPostId(@Path("post_param") int post_param);

    //@Headers("Accept-Charset: utf-8")
    @POST("/add/comment")
    Call<Integer> addNewComment(@Body PostCommentsEntity comment);

    //@Headers("Accept-Charset: utf-8")
    @POST("/add/comment/response/{postId}")
    Call<Integer> addNewCommentResponse(@Body PostCommentsEntity response,
                                        @Path("postId") int postId);

    //@Headers("Accept-Charset: utf-8")
    @PUT("/users")
    Call<UserEntity> updateUserData(@Body UserEntity user);

    //@Headers("Accept-Charset: utf-8")
    @GET("/notifications/unread/{uId}")
    Call<Integer> getUnreadNotificationsCount(@Path("uId") int uId);

    //@Headers("Accept-Charset: utf-8")
    @GET("/notifications/{userId}")
    Call<List<NotificationsEntity>> getAllNotificationsByUserId(@Path("userId") int userId);

    //@Headers("Accept-Charset: utf-8")
    @PUT("/notifications/reading/{nId}")
    Call<Integer> readNotification(@Path("nId") int nId);

   // @Headers("Accept-Charset: utf-8")
    @GET("/users/exists")
    Call<Boolean> emailIsExists(@Query("email") String email);

   // @Headers("Accept-Charset: utf-8")
    @GET("/users/fb/{facebookId}")
    Call<UserEntity> getUserByFacebookId(@Path("facebookId") String facebookId);

    //@Headers("Accept-Charset: utf-8")
    @GET("/users/ggl/{googleId}")
    Call<UserEntity> getUserByGoogleId(@Path("googleId") String googleId);

    //@Headers("Accept-Charset: utf-8")
    @GET("/users/ta")
    Call<UserEntity> getUserByEmail(@Query("email") String email);

    //@Headers("Accept-Charset: utf-8")
    @POST("/users/add")
    Call<UserEntity> saveUser(@Body UserEntity userEntity);

    //@Headers("Accept-Charset: utf-8")
    @GET("/params/{userId}")
    Call<Hashtable<String, Integer>> getProfileParams(@Path("userId") int userId);

    //@Headers("Accept-Charset: utf-8")
    @GET("/postids/{userId}")
    Call<List<Integer>> getIdsByUser(@Path("userId") int userId);

    //@Headers("Accept-Charset: utf-8")
    @GET("/followers/{followOn}")
    Call<List<FollowingEntity>> getAllFollowers(@Path("followOn") int followOn);

    //@Headers("Accept-Charset: utf-8")
    @POST("/followers/follow")
    Call<FollowingEntity> followOn(@Body FollowingEntity followingEntity);

    //@Headers("Accept-Charset: utf-8")
    @DELETE("/followers/unfollow/{userId}/{curUserId}")
    Call<Void> unfollowOn(@Path("userId") int userId, @Path("curUserId") int curUserId);

    //@Headers("Accept-Charset: utf-8")
    @GET("/favorites/{userId}")
    Call<List<PostFavoritesEntity>> getAllFavoritesByUser(@Path("userId") int userId);

    //@Headers("Accept-Charset: utf-8")
    @POST("/favorites/add")
    Call<PostFavoritesEntity> addFavorite(@Body PostFavoritesEntity postFavoritesEntity);

    //@Headers("Accept-Charset: utf-8")
    @DELETE("/favorites/delete/{id}")
    Call<Void> deleteFavorite(@Path("id") int id);

    //@Headers("Accept-Charset: utf-8")
    @POST("/invites/add")
    Call<InviteEntity> addInvite(@Body InviteEntity inviteEntity);

    //@Headers("Accept-Charset: utf-8")
    @GET("/invites/from/{userId}")
    Call<List<InviteEntity>> getAllInvitesByFrom(@Path("userId") int userId);

    //@Headers("Accept-Charset: utf-8")
    @GET("/invites/to/{userId}")
    Call<List<InviteEntity>> getAllInvitesByTo(@Path("userId") int userId);

    //@Headers("Accept-Charset: utf-8")
    @PUT("/post/blocking/{postId}/{value}")
    Call<Integer> blockFullPost(@Path("postId") int postId, @Path("value") boolean value);

    //@Headers("Accept-Charset: utf-8")
    @GET("/tags/distinct")
    Call<List<PostTagsEntity>> getDistinctTags();
}
