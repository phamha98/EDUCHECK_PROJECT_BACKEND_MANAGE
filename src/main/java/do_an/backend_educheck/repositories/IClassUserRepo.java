package do_an.backend_educheck.repositories;

import do_an.backend_educheck.models.class_entity.ClassUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IClassUserRepo extends JpaRepository<ClassUserEntity, Long> {
    Optional<ClassUserEntity> findByClassIdAndStudentId(Long classId, Long studentId);
    List<ClassUserEntity> findByStudentId(Long studentId);

    @Query("SELECT a from ClassUserEntity a where a.classId in ?1")
    List<ClassUserEntity> findClassUsers(List<Long> ids);

    void deleteAllByClassId(Long id);

    @Modifying
    @Query("delete from ClassUserEntity e where e.classId in ?1 and e.studentId in ?2")
    void deleteByClassIdAndStudentId(Long classId, Long studentId);

    @Query("SELECT c.studentId from ClassUserEntity c where c.classId = ?1 and c.studentId <> ?2")
    List<Long> findUsersInClass(Long classId, Long studentId);
}
