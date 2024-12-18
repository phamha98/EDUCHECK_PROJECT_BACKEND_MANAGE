package do_an.backend_educheck.dtos.class_dto;

import do_an.backend_educheck.dtos.quiz_dto.QuizDto;
import do_an.backend_educheck.models.class_entity.ClassEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PageClass {
    List<ClassEntity> classes;
    Integer totalPages;
    List<QuizDto> quizzes;

    public PageClass(List<ClassEntity> classes, Integer totalPages) {
        this.classes = classes;
        this.totalPages = totalPages;
    }
}
