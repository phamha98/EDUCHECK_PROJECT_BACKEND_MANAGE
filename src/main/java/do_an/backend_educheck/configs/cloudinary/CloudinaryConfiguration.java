package do_an.backend_educheck.configs.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CloudinaryConfiguration {
    private final CloudinaryEnvironmentConfig environmentConfig;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", environmentConfig.getCloud_name(),
                "api_key", environmentConfig.getApi_key(),
                "api_secret", environmentConfig.getApi_secret()));
    }
}
