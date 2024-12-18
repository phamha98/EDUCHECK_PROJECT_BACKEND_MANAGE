package do_an.backend_educheck.controllers.auth_controller;

import do_an.backend_educheck.controllers.ApiV1Controller;
import do_an.backend_educheck.dtos.auth_dto.AuthRequest;
import do_an.backend_educheck.dtos.auth_dto.AuthResponse;
import do_an.backend_educheck.dtos.auth_dto.ResponseToUser;
import do_an.backend_educheck.exceptions.ArgumentException;
import do_an.backend_educheck.exceptions.ResourceNotFoundException;
import do_an.backend_educheck.models.user_entity.UserEntity;
import do_an.backend_educheck.services.auth_service.IAuthService;
import do_an.backend_educheck.services.auth_service.TokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@Validated
@RequiredArgsConstructor
@ApiV1Controller
@RequestMapping(value = "/", produces = "application/json")
public class AuthController {
    private final String REGEX_EMAIL = "\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]+\\b";
    private final String REGEX_PASSWORD = "^(?=.*[A-Z])(?=.*[!@#$&*])(?=.*[0-9])(?=.*[a-z]).{8,}$";
    private final PasswordEncoder passwordEncoder;

    private final IAuthService iAuthService;
    private final TokenProvider tokenProvider;

    @Value("${app.auth.tokenCookieName}")
    public String tokenCookieName;


    @PostMapping("sign-up")
    public ResponseToUser createUser(@RequestBody UserEntity userEntity) {
        try {
            Pattern patternEmail = Pattern.compile(REGEX_EMAIL, Pattern.CASE_INSENSITIVE);
            Pattern patternPassword = Pattern.compile(REGEX_PASSWORD, Pattern.CASE_INSENSITIVE);
            Matcher matcherEmail = patternEmail.matcher(userEntity.getEmail());
            Matcher matcherPassword = patternPassword.matcher(userEntity.getPassword());
            if (!matcherEmail.matches() || userEntity.getRole()
                    .toLowerCase()
                    .equals("admin")) {
                return new ResponseToUser(false,  "Email không hợp lệ!");
            }
            if (!matcherPassword.matches()) {
                return new ResponseToUser(false,  "Password cần có ký tự đặc biệt, chữ,số!");
            }
            String encodedPassword = passwordEncoder.encode(userEntity.getPassword());
            userEntity.setPassword(encodedPassword);

            final var user = iAuthService.fetchUser(userEntity.getEmail(), userEntity.getUsername());
            if (user == null) {
                iAuthService.saveUser(userEntity);
            } else {
                return new ResponseToUser(false,  "Email hoặc Username đã tồn tại!");
            }
            return new ResponseToUser(true,  "Bạn đã đăng ký thành công!");
        } catch (ArgumentException ae) {
            return new ResponseToUser(false,  ae.getMessage());
        }
    }

    @PostMapping("sign-in")
    public AuthResponse authLogin(@RequestBody @Valid AuthRequest authRequest, HttpServletResponse response) {
        final String email = authRequest.getEmail();
        final String password = authRequest.getPassword();
        final var user = iAuthService.getUserByEmail(email);

        if (user == null) {
            throw new ResourceNotFoundException("Email " + email);
        }

        boolean isMatcher = passwordEncoder.matches(password, user.getPassword());
        if (!isMatcher) {
            throw new ResourceNotFoundException("Email " + email);
        } else {
            final var token = tokenProvider.createJwtToken(email, user.getRole());
            iAuthService.addTokenCookie(response, tokenCookieName, token);
            return new AuthResponse(user, token);
        }
    }

    @PostMapping("logout")
    public ResponseToUser authLogout(HttpServletResponse response) {
        iAuthService.logoutUser(response);

        return new ResponseToUser(true,  "Đăng xuất thành công");
    }

}
