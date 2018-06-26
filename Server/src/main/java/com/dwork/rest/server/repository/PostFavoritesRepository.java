package com.dwork.rest.server.repository;

import com.dwork.rest.server.entity.PostFavoritesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostFavoritesRepository extends JpaRepository<PostFavoritesEntity, Integer> {

    @Query("select f from PostFavoritesEntity f where f.userId.user_id = ?1")
    List<PostFavoritesEntity> findAllByUserId(int userId);

    @Query("select f from PostFavoritesEntity  f where f.userId.user_id = ?1 and f.post_id.post_id = ?2")
    List<PostFavoritesEntity> condition(int userId, int postId);
}
