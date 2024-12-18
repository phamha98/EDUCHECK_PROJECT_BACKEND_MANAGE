package do_an.backend_educheck.repositories;

import do_an.backend_educheck.models.user_entity.EWaitingR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEWRRepo extends JpaRepository<EWaitingR,Long> {

    EWaitingR findByEmail(String email);

    void deleteByEmail(String email);

    @Modifying
    @Query("delete from EWaitingR e where e.id in ?1")
    void deleteUserEmailWithIds(List<Long> ids);
}
