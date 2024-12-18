package do_an.backend_educheck.configs.oathu2;


import do_an.backend_educheck.configs.EnvironmentConfig;
import do_an.backend_educheck.configs.jwt.JWTAuthorizationFilter;
import do_an.backend_educheck.services.auth_service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class Oauth2Config {
    private final IAuthService iAuthService;
    private final JWTAuthorizationFilter jwtAuthorizationFilter;
    private final EnvironmentConfig environmentConfig;


    private String[] publicEndpoints = {
            "/login", "/signup", "/home",
            "/forgot-password", "/reset-password", "/access-reset",
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(publicEndpoints).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandlingConfigurer -> {
                    exceptionHandlingConfigurer.authenticationEntryPoint(
                            new Oauth2AuthenticationEntrypoint());
                })
                .oauth2Login(customizer -> {
                    customizer
                            .successHandler(new Oauth2LoginSuccessHandler(iAuthService, environmentConfig));
                });
        return http.build();
    }
}
