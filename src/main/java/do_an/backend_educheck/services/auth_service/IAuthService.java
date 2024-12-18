package do_an.backend_educheck.services.auth_service;


import do_an.backend_educheck.dtos.auth_dto.UserResponse;
import do_an.backend_educheck.models.user_entity.UserEntity;
import jakarta.servlet.http.HttpServletResponse;

public interface IAuthService {

    UserResponse saveUser(UserEntity userEntity);

    UserEntity fetchUser(String email, String username);

    UserEntity getUserByEmail(String email);

    void loginWithGoogle(UserEntity appUser, HttpServletResponse response);

    void logoutUser(HttpServletResponse response);

    void addTokenCookie(HttpServletResponse response, final String cookieName, final String token);
}
