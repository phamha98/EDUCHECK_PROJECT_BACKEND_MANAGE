package do_an.backend_educheck.services;

import do_an.backend_educheck.dtos.ApiResponse;
import do_an.backend_educheck.dtos.quiz_dto.QuizDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiService {
    private final WebClient webClient;

    public List<QuizDto> getAllQuizzesByClassAndUser(List<Long> classIds, Long userId) {
        ApiResponse<List<QuizDto>> response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("all-class-quizzes/{userId}")
                        .queryParam("ids", classIds)
                        .build(userId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<QuizDto>>>() {})
                .block();

        return response != null ? response.getData() : new ArrayList<>();
    }
}
