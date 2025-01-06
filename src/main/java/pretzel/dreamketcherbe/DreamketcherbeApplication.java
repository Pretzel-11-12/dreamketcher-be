package pretzel.dreamketcherbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DreamketcherbeApplication {

    public static void main(String[] args) {
        SpringApplication.run(DreamketcherbeApplication.class, args);
    }

}
