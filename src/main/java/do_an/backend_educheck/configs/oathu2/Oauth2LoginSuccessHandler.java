package do_an.backend_educheck.configs.oathu2;

import do_an.backend_educheck.configs.EnvironmentConfig;
import do_an.backend_educheck.models.user_entity.UserEntity;
import do_an.backend_educheck.services.auth_service.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class Oauth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final IAuthService iAuthService;
    private final EnvironmentConfig environmentConfig;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        log.info("Authentication: {}", authentication);
        DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();
        UserEntity appUser = UserEntity.fromGoogleUser(oidcUser);

        iAuthService.loginWithGoogle(appUser, response);

        AppAuthenticationToken token = new AppAuthenticationToken(appUser);
        SecurityContextHolder.getContext().setAuthentication(token);
        response.sendRedirect(environmentConfig.getFrontEndDomain() + "/home");
    }
}
