package do_an.backend_educheck.repositories;

import do_an.backend_educheck.models.feedback_entity.FeedbackEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFeedbackRepo extends JpaRepository<FeedbackEntity, Long> {
    Page<FeedbackEntity> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    @Query("DELETE from FeedbackEntity f where f.id in ?1")
    @Modifying
    void deleteFeedbacksByIds(List<Long> ids);
}
