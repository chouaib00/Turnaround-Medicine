package com.dwork.rest.server.repository;

import com.dwork.rest.server.entity.InviteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InviteRepository extends JpaRepository<InviteEntity, Integer> {

    @Query("select i from InviteEntity i where i.inviteTo.user_id = ?1")
    List<InviteEntity> findAllByInviteTo(int userId);

    @Query("select i from InviteEntity i where i.inviteFrom.user_id = ?1")
    List<InviteEntity> findAllByInviteFrom(int userId);

    @Query("select count(i) from InviteEntity i where i.inviteFrom.user_id = ?1 and i.inviteTo.user_id = ?2")
    int exists(int from, int to);
}
