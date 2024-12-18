package do_an.backend_educheck.models.class_entity;

import do_an.backend_educheck.models.BaseEnt;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_class")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassEntity extends BaseEnt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Mã lớp không thể trống")
    private String classCode;

    @NotBlank(message = "Tên lớp không thể trống")
    private String className;

    @NotBlank(message = "Phần không thể trống")
    private String section;

    @NotBlank(message = "Chủ đề không thể trống")
    private String subject;

    @NotNull
    private Long userId;
    private String thumbnail;

}
