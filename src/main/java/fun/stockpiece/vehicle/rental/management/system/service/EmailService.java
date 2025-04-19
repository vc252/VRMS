package fun.stockpiece.vehicle.rental.management.system.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import fun.stockpiece.vehicle.rental.management.system.dto.ApiException;
import fun.stockpiece.vehicle.rental.management.system.dto.OtpVerificationDTO;
import fun.stockpiece.vehicle.rental.management.system.model.Otp;
import fun.stockpiece.vehicle.rental.management.system.repository.OtpRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;

@AllArgsConstructor
@Service
public class EmailService {
    private final SendGrid sg;
    private final OtpRepository otpRepository;
    private final UserService userService;

    public HttpStatus sendEmailVerificationOTP(String toMail) throws IOException {
        Mail mail = new Mail();

        Email fromEmail = new Email();
        fromEmail.setName("stockpiece");
        fromEmail.setEmail("noreply@stockpiece.fun");
        mail.setFrom(fromEmail);

        mail.setTemplateId("d-550f1ef8b5b2499ebdcd2f95ebac8742");

        String OTP = generateSixDigitOtp(toMail);

        Personalization personalization = new Personalization();
        personalization.addDynamicTemplateData("otp", OTP);
        personalization.addTo(new Email(toMail));
        mail.addPersonalization(personalization);

        Request request = new Request();

        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sg.api(request);

        if (response.getStatusCode() >= 400) {
            throw new ApiException("error in sending the mail",response.getStatusCode(),response.getBody());
        }

        System.out.println("email send successfully");
        System.out.println(response);

        return HttpStatus.resolve(response.getStatusCode());
    }

    public boolean verifyOtp(String email, String otp) {
        Optional<Otp> existingOtp = otpRepository.findByEmail(email);
        return existingOtp.map(value -> value.getCode().equals(otp)).orElse(false);
    }

    private String generateSixDigitOtp(String email) {
        SecureRandom random = new SecureRandom();

        Optional<Otp> existingOtp = otpRepository.findByEmail(email);

        String code;
        do {
            code = String.valueOf(random.nextInt(1000000));
        } while (this.otpExists(code));

        Otp newOtp = Otp.builder()
                .email(email)
                .code(code)
                .createdAt(Instant.now())
                .build();
        //this will automatically remove the old otp by email if it exists
        otpRepository.save(newOtp);

        return code;
    }

    private boolean otpExists(String otp) {
        return otpRepository.existsByCode(otp);
    }

    private boolean otpExistsForEmail(String email) {
        return otpRepository.existsByEmail(email);
    }
}
