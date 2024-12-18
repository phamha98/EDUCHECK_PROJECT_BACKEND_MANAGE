package do_an.backend_educheck.services.feedback_service;

import do_an.backend_educheck.dtos.auth_dto.ResponseToUser;
import do_an.backend_educheck.models.feedback_entity.FeedbackEntity;
import do_an.backend_educheck.repositories.IFeedbackRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedbackService implements IFeedbackService {
    private final IFeedbackRepo iFeedbackRepo;

    @Override
    public Boolean saveFeedback(FeedbackEntity feedbackEntity) {
        try {
            iFeedbackRepo.save(feedbackEntity);
            return true;
        } catch (Exception ex) {
            return false;
        }

    }

    @Override
    public Page<FeedbackEntity> searchFeedbacks(String username, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        if (username.isBlank()) {
            return iFeedbackRepo.findAll(pageable);
        }

        return iFeedbackRepo.findByUsernameContainingIgnoreCase(username, pageable);
    }

    @Override
    @Transactional
    public ResponseToUser deleteFeedbacksByIds(List<Long> ids) {
        try {
            if (ids != null) {
                iFeedbackRepo.deleteFeedbacksByIds(ids);
            }
            return new ResponseToUser(true, "Xóa thành công");
        } catch (Exception ex) {
            System.out.println("ex::"+ex.getMessage());
            return new ResponseToUser(false, "Xóa thất bại");
        }
    }
}
