package do_an.backend_educheck.controllers.feedback_controller;

import do_an.backend_educheck.controllers.ApiV1Controller;
import do_an.backend_educheck.dtos.auth_dto.ResponseToUser;
import do_an.backend_educheck.models.feedback_entity.FeedbackEntity;
import do_an.backend_educheck.services.feedback_service.IFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Validated
@RequiredArgsConstructor
@ApiV1Controller
@RequestMapping(value = "/", produces = "application/json")
public class FeedbackController {
    private final IFeedbackService iFeedbackService;

    @PostMapping("send-feedback")
    public Boolean saveFeedback(@RequestBody FeedbackEntity feedbackEntity) {
        return iFeedbackService.saveFeedback(feedbackEntity);
    }

    @GetMapping("search-feedback")
    public List<FeedbackEntity> searchFeedbacks(@RequestParam String username,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        Page<FeedbackEntity> feedbacks = iFeedbackService.searchFeedbacks(username, page, size);
        return feedbacks.getContent();
    }


    @DeleteMapping("delete-feedback")
    public ResponseToUser deleteFeedbacks(@RequestParam String ids) {
        String[] splitIds = ids.split(",");
        List<Long> longIds = Arrays.stream(splitIds).map(Long::valueOf).collect(Collectors.toList());
        return iFeedbackService.deleteFeedbacksByIds(longIds);
    }
}
