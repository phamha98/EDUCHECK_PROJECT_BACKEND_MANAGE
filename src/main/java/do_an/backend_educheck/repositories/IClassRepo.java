package do_an.backend_educheck.repositories;

import do_an.backend_educheck.models.class_entity.ClassEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IClassRepo extends JpaRepository<ClassEntity, Long> {
    List<ClassEntity> findByUserId(Long id);

    @Query("SELECT c FROM ClassEntity c WHERE c.id IN :ids")
    List<ClassEntity> findAllClassByIds(@Param("ids") List<Long> ids);

    @Query("SELECT c FROM ClassEntity c WHERE c.classCode LIKE %:searchText% OR c.className LIKE %:searchText% and c.userId=:userId ORDER BY c.updated")
    Page<ClassEntity> findClassByUserIdAndSearchText(@Param("searchText") String searchText, @Param("userId") Long userId,
                                        Pageable pageable);

    Page<ClassEntity> findAllByUserId(Long userId, Pageable pageable);

    @Query(value = """
                SELECT MONTH(c.created) AS month, COUNT(c.id) AS total
                FROM tbl_class c
                WHERE YEAR(c.created) = :year
                GROUP BY MONTH(c.created)
                ORDER BY MONTH(c.created)
            """, nativeQuery = true)
    List<Object[]> countClassesByMonth(int year);

    @Query(value = """
                SELECT MONTH(c.created) AS month, COUNT(c.id) AS total
                FROM tbl_class c
                WHERE YEAR(c.created) = :year AND c.user_id= :userId
                GROUP BY MONTH(c.created)
                ORDER BY MONTH(c.created)
            """, nativeQuery = true)
    List<Object[]> countClassesByMonthAndUserId(int year, Long userId);

    Optional<ClassEntity> findById(Long id);

    void deleteById(Long id);
}
