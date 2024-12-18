package do_an.backend_educheck.dtos.user_dto;

import do_an.backend_educheck.dtos.auth_dto.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MembersInClass {
    UserResponse teacher;
    List<UserResponse> students;
}
