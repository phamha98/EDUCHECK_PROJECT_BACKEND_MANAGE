package do_an.backend_educheck.models.avatar_entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_avatars")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvatarEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String publicId;
    private String url;
    private Long userId;
    private Long classId;
}
