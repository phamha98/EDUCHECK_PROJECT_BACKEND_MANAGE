package do_an.backend_educheck.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class EnvironmentConfig {
    private List<String> corsEndPoint;
    private String frontEndDomain;
    private String backEndQuizDomain;
}
