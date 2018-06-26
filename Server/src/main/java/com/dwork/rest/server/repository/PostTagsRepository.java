package com.dwork.rest.server.repository;

import com.dwork.rest.server.entity.PostTagsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface PostTagsRepository extends JpaRepository<PostTagsEntity, Integer> {

    @Query("select p from PostTagsEntity p where p.post_id.post_id = :post_param")
    List<PostTagsEntity> findPostTagsEntitiesByPost_id(@Param("post_param") int post_param);

    @Query("select p.post_id.post_id from PostTagsEntity p where p.tag_value = :tag_param")
    List<Integer> findPostIdsByTag_value(@Param("tag_param") String tag_param);

    @Query("select distinct p from PostTagsEntity p")
    List<PostTagsEntity> findDistinctTags();
}
