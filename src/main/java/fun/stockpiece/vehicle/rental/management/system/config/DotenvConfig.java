package fun.stockpiece.vehicle.rental.management.system.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {
    //this will initialize this after
    @PostConstruct
    public void init() {
        Dotenv dotenv = Dotenv.load();
        for (DotenvEntry entry: dotenv.entries()) {
            System.setProperty(entry.getKey(),entry.getValue());
        }
    }
}
