package do_an.backend_educheck.services.auth_service;

import do_an.backend_educheck.dtos.auth_dto.UserResponse;
import do_an.backend_educheck.models.user_entity.UserEntity;
import do_an.backend_educheck.repositories.IUserRepo;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final IUserRepo iUserRepo;
    private final TokenProvider tokenProvider;

    @Value("${app.auth.tokenCookieName}")
    public String tokenCookieName;

    @Override
    public UserResponse saveUser(UserEntity userEntity) {
        iUserRepo.save(userEntity);
        return new UserResponse(userEntity);
    }

    @Override
    public UserEntity fetchUser(String email, String username) {
        return iUserRepo.findByEmailOrUsername(email, username);
    }

    @Override
    public UserEntity getUserByEmail(String email) {
        return iUserRepo.findByEmail(email);
    }

    public void loginWithGoogle(UserEntity appUser, HttpServletResponse response) {
        String email = appUser.getEmail();
        final var existUser = this.getUserByEmail(email);

        final var token = tokenProvider.createJwtToken(appUser.getEmail(), "student");
        addTokenCookie(response, tokenCookieName, token);

        if (existUser == null) {
            this.saveUser(appUser);
        }
    }

    public void logoutUser(HttpServletResponse response) {
        tokenProvider.removeTokenOnCookie(tokenCookieName, response);
    }

    public void addTokenCookie(HttpServletResponse response, final String cookieName, final String token) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, token)
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7 days
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
