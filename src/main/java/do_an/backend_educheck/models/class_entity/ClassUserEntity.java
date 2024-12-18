package do_an.backend_educheck.models.class_entity;

import do_an.backend_educheck.models.BaseEnt;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "tbl_class_user") //tbl_class_user
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassUserEntity extends BaseEnt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long studentId;
    @NotNull
    private Long classId;
}
