package do_an.backend_educheck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BackendEducheckApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendEducheckApplication.class, args);
	}

}
