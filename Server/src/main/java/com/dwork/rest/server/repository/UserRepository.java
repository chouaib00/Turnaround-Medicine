package com.dwork.rest.server.repository;

import com.dwork.rest.server.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    @Query("select count(*) > 0 from UserEntity where user_email = ?1")
    boolean emailIsExists(String email);

    @Query("select user from UserEntity user where user.facebook_id = ?1")
    UserEntity getUserEntityByFacebook_id(String facebookId);

    @Query("select user from UserEntity user where user.google_id = ?1")
    UserEntity getUserEntityByGoogle_id(String googleId);

    @Query("select user from UserEntity user where user.user_email = ?1")
    UserEntity getUserEntityByUser_email(String email);

    @Query("select count(user) from UserEntity user where user.user_status = 'us'")
    Integer getUserCount();

    @Query("select count(user) from UserEntity user where user.user_status = 'dc'")
    Integer getDoctorCount();
}
