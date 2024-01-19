package source_files;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import source_files.data.models.vehicleEntities.vehicleFeatures.CarFeatures.CarModelEntity;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CarModelEntity getCarModelEntity() {
        return new CarModelEntity();
    }
    //DENEME
}