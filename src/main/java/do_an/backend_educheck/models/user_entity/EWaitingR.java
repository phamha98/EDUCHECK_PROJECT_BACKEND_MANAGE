package do_an.backend_educheck.models.user_entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_EWaitingR")
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class EWaitingR {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_email")
    private String email;
}
