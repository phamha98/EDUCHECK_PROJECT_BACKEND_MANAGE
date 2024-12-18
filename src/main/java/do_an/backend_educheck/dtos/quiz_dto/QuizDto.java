package do_an.backend_educheck.dtos.quiz_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class QuizDto {
    @JsonProperty("_id")
    private String _id;
    private String quizzTitle;
    private String quizzDescription;
    private String classId;
    private String testCode;
    private String updatedAt;
}
