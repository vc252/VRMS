package fun.stockpiece.vehicle.rental.management.system.config;

import com.sendgrid.SendGrid;
import fun.stockpiece.vehicle.rental.management.system.dto.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class SendGridConfig {
    @Value("${SENDGRID_API_KEY}")
    private String apiKey;
    @Bean
    public SendGrid sendGrid() {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new ApiException("no api key", HttpStatus.INTERNAL_SERVER_ERROR.value(),"environment properties were not set properly");
        }
        return new SendGrid(apiKey);
    }
}
