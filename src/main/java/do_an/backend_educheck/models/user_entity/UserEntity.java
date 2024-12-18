package do_an.backend_educheck.models.user_entity;

import do_an.backend_educheck.dtos.auth_dto.UserResponse;
import do_an.backend_educheck.models.BaseEnt;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_user")
@Builder
@ToString
public class UserEntity extends BaseEnt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String email;

    private String address;

    private String gender;

    private String avatar;

    private String sdt;

    private String password;

    private Boolean favorite;

    private String role;

    public UserEntity(UserResponse userResponse) {
        this.id = userResponse.id();
        this.username = userResponse.username();
        this.email = userResponse.email();
        this.address = userResponse.address();
        this.gender = userResponse.gender();
        this.avatar = userResponse.avatar();
        this.sdt = userResponse.sdt();
        this.favorite = userResponse.favorite();
        this.role = userResponse.role();
    }

    public static UserEntity fromGoogleUser(DefaultOidcUser googleUser) {
        UserEntity appUser = new UserEntity();
        appUser.username = googleUser.getFullName();
        appUser.email = googleUser.getEmail();
        appUser.avatar = googleUser.getPicture();
        appUser.role = "student";
        appUser.gender = "male";
        return appUser;
    }

}