package do_an.backend_educheck.services.feedback_service;

import do_an.backend_educheck.dtos.auth_dto.ResponseToUser;
import do_an.backend_educheck.models.feedback_entity.FeedbackEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IFeedbackService {
    Boolean saveFeedback(FeedbackEntity feedbackEntity);

    Page<FeedbackEntity> searchFeedbacks(String username, int page, int size);

    ResponseToUser deleteFeedbacksByIds(List<Long> ids);
}
