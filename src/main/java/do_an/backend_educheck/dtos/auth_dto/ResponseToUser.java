package do_an.backend_educheck.dtos.auth_dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseToUser {
    private Boolean status;
    private Object message;
}
