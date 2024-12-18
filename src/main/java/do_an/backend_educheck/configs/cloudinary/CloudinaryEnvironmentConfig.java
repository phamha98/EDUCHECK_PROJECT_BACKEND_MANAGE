package do_an.backend_educheck.configs.cloudinary;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "cloudinary")
public class CloudinaryEnvironmentConfig {
    private String cloud_name;
    private String api_key;
    private String api_secret;
}
