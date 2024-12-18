package do_an.backend_educheck.dtos.user_dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PasswordRequest {
    @NotNull
    private String email;
    @NotNull
    private String oldPassword;
    @NotNull
    private String password;
}
