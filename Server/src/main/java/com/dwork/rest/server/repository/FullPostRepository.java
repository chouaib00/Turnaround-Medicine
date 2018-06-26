package com.dwork.rest.server.repository;

import com.dwork.rest.server.entity.FullPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface FullPostRepository extends JpaRepository<FullPostEntity, Integer> {
    @Query("select MAX(p.post_id)from FullPostEntity p")
    int findMax();

    @Query("select MIN(p.post_id)from FullPostEntity p")
    int findMin();

    @Query("select p from FullPostEntity p" +
            " where p.user_id.user_id" +
            "=:user_param order by p.post_date desc")
    List<FullPostEntity> findPostsByUser_id(@Param("user_param") int user_param);

    @Query("select p from FullPostEntity p" +
            " where p.user_id.user_id" +
            "=:user_param and p.post_id > :date_param")
    List<FullPostEntity> findPostsByUser_idAndPost_date(@Param("user_param") int user_param,
                                                    @Param("date_param") int date_param);

    @Query("select p from FullPostEntity p where p.post_id = :post_param")
    FullPostEntity findFullPostEntityByPost_id(@Param("post_param") int post_param);


    @Query("select p.post_id from FullPostEntity p where" +
            " p.description like CONCAT('%',:descParam,'%')" +
            " or p.description like CONCAT('%',LOWER(:descParam),'%')" +
            " or p.description like CONCAT('%',UPPER(:descParam),'%')")
    List<Integer> getIdsByDescription(@Param("descParam") String descParam);

    @Query("select p.post_id from FullPostEntity p where" +
            " (p.description like CONCAT('%',:descParam,'%')" +
            " or p.description like CONCAT('%',LOWER(:descParam),'%')" +
            " or p.description like CONCAT('%',UPPER(:descParam),'%'))" +
            " and p.post_status = 'pbl' ")
    List<Integer> getIdsByDescriptionWithSecurity(@Param("descParam") String descParam);

    @Query("select p.post_id from FullPostEntity p where p.user_id.user_id = ?1")
    List<Integer> getIdsByUser(int userId);

    @Query("select count(p) from FullPostEntity p where p.user_id.user_id = :uId")
    int getPostsCountByUser_id(@Param("uId") int uId);

    @Query("select count(p) from FullPostEntity p where p.user_id.user_status='us'")
    Integer getUserPostsCount();

    @Query("select count(p) from FullPostEntity p where p.user_id.user_status='dc'")
    Integer getDoctorPostsCount();
}
