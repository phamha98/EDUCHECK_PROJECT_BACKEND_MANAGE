package do_an.backend_educheck.repositories;

import do_an.backend_educheck.models.avatar_entity.AvatarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IAvatarsRepo extends JpaRepository<AvatarEntity, Long> {
    void deleteAllByUserId(Long userId);
    void deleteAllByClassId(Long classId);

    Optional<AvatarEntity> findByUserId(Long id);
    Optional<AvatarEntity> findByClassId(Long classId);

    @Query("SELECT a from AvatarEntity a where a.classId in ?1")
    List<AvatarEntity> findClassAvatars(List<Long> ids);

    @Query("DELETE from AvatarEntity a where a.classId in ?1")
    @Modifying
    void deleteClassAvatars(List<Long> ids);
}
