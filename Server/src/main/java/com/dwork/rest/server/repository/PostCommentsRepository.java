package com.dwork.rest.server.repository;

import com.dwork.rest.server.entity.PostCommentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostCommentsRepository extends JpaRepository<PostCommentsEntity, Integer> {

    @Query("select distinct p from PostCommentsEntity p where p.post_id = :post_param and p.childs is null")
    List<PostCommentsEntity> findPostCommentsEntitiesByPost_id(@Param("post_param") int post_param);

    @Query("select count(p) from PostCommentsEntity p where p.post_id = :post_param")
    int getCountComments(@Param("post_param") int post_param);

    @Query("select count(p) from PostCommentsEntity p where p.user_id.user_id = :pId")
    int getCountCommentsByUser_id(@Param("pId") int pId);

    @Modifying
    @Transactional
    @Query("delete from PostCommentsEntity  p where p.post_id = ?1")
    void deleteCommentsByPost_id(int id);
}
