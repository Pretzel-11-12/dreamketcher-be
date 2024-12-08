package pretzel.dreamketcherbe;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableBatchProcessing
public class DreamketcherbeApplication {

	public static void main(String[] args) {
		SpringApplication.run(DreamketcherbeApplication.class, args);
	}

}
