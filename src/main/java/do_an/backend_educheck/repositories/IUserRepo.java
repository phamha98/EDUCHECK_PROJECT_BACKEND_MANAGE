package do_an.backend_educheck.repositories;

import do_an.backend_educheck.models.user_entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepo extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmailOrUsername(String email, String username);

    UserEntity findByEmail(String email);

    Optional<UserEntity> findById(Long id);

    @Query("SELECT u FROM UserEntity u WHERE (u.username = :username OR u.email = :email) AND u.id <> :id")
    Optional<UserEntity> findByUsernameOrEmailAndNotId(@Param("username") String username, @Param("email") String email,
                                                       @Param("id") Long id);

    @Query("SELECT u from UserEntity u where u.id in ?1")
    List<UserEntity> findUsersInClass(List<Long> ids);

    @Query("SELECT u from UserEntity u where u.id in ?1 AND u.username like %?2%")
    List<UserEntity> findUsersInClassByUsername(List<Long> ids, String username);

    @Query(value = """
        SELECT MONTH(u.created) AS month, COUNT(u.id) AS total
        FROM tbl_user u
        WHERE YEAR(u.created) = :year 
        GROUP BY MONTH(u.created)
        ORDER BY MONTH(u.created)
    """,nativeQuery = true)
    List<Object[]> countUsersByMonth(int year);

    @Query(value = """
        SELECT MONTH(u.created) AS month, COUNT(u.id) AS total
        FROM tbl_user u
        WHERE YEAR(u.created) = :year and u.id in :userIds
        GROUP BY MONTH(u.created)
        ORDER BY MONTH(u.created)
    """,nativeQuery = true)
    List<Object[]> countUsersByMonthAndUserIds(int year, List<Long> userIds);

    @Query("SELECT u FROM UserEntity u WHERE u.id > :lastId AND u.updated >= :lastUpdated ORDER BY u.id")
    Page<UserEntity> findUpdatedUsers(@Param("lastId") Long lastId, @Param("lastUpdated") LocalDateTime lastUpdated,
                                      Pageable pageable);

    Page<UserEntity> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "DELETE from tbl_user where id in :ids", nativeQuery = true)
    void deleteUsersById(@Param("ids") List<Long> ids);

    @Transactional
    @Modifying
    @Query(value = "DELETE from tbl_class where user_id in :ids", nativeQuery = true)
    void deleteClassesByUserId(@Param("ids") List<Long> ids);

    @Transactional
    @Modifying
    @Query(value = "DELETE from tbl_class_user where student_id in :ids", nativeQuery = true)
    void deleteClassUserByStudentId(@Param("ids") List<Long> ids);
}
