package com.dwork.rest.server.repository;

import com.dwork.rest.server.entity.FollowingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface FollowingEntityRepository extends JpaRepository<FollowingEntity, Integer> {

    @Query("select f from FollowingEntity f where f.followOn.user_id = ?1")
    List<FollowingEntity> findAllByFollowOn(int followOn);

    @Modifying
    @Transactional
    @Query("delete from FollowingEntity f where f.followOn.user_id = ?1 and f.userId.user_id = ?2")
    void unfollowOnUser(int userId, int curUserId);
}
