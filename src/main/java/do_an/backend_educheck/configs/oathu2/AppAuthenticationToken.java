package do_an.backend_educheck.configs.oathu2;

import do_an.backend_educheck.models.user_entity.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.Collection;

public class AppAuthenticationToken implements Authentication {
    private final UserEntity appUser;
    private final boolean authenticated;
    private Collection<GrantedAuthority> authorities;
    private WebAuthenticationDetails details;

    public AppAuthenticationToken(UserEntity userEntity) {
        this.appUser = userEntity;
        this.authenticated = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return details;
    }

    @Override
    public Object getPrincipal() {
        return appUser;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Use constructor to set authenticated to true");
    }

    @Override
    public String getName() {
        return appUser.getUsername();
    }
}
