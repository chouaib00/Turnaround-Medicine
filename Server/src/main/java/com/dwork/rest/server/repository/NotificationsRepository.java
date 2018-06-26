package com.dwork.rest.server.repository;

import com.dwork.rest.server.entity.NotificationsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationsRepository extends JpaRepository<NotificationsEntity, Integer> {

    @Query("select count(n.notificationId)" +
            " from NotificationsEntity n where n.notificationRead = 0" +
            " and n.postId.user_id.user_id = :userId and n.userFromId.user_id <> :userId")
    int getUnread(@Param("userId") int userId);

    @Query("select n from NotificationsEntity n where n.postId.user_id.user_id = :userId")
    List<NotificationsEntity> getAllByUser(@Param("userId") int userId);

    @Modifying
    @Transactional
    @Query("delete from NotificationsEntity n where n.postId.post_id = ?1")
    void deleteNotificationByPostId(int id);
}
