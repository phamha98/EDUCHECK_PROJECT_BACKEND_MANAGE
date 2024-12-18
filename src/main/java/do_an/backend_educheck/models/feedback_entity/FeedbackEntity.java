package do_an.backend_educheck.models.feedback_entity;

import do_an.backend_educheck.models.BaseEnt;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_feedbacks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackEntity extends BaseEnt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String type;

    private String feedback;
}
