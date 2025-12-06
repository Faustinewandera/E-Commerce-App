package Wandera.E_Commerce.App.EmailConfig;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);


    @Async
    public void sendEmailToSeller(String to, String subject, String body) {

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);

            log.info("Email successfully sent to {}", to);

        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
        }
    }


    // this sends email containing  the otp code for reset password that user forget
    @Async
    public void sendEmailResetToken(String to, String subject, String templateName, Map<String, String> variables)
            throws MessagingException, IOException {

        String templatePath = "Template/" + templateName;

        // this loads HTML template
        ClassLoader classLoader = getClass().getClassLoader();
        String htmlContent = new String(classLoader.getResourceAsStream(templatePath).readAllBytes());

        // Replace template values
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            htmlContent = htmlContent.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    //this sends verification token to the user vie email
    @Async
    public void sendOtpRegisterVerification(String to, String subject, String templateName, Map<String, String> variables)
            throws MessagingException, IOException {

        String templatePath = "Template/" + templateName;

        // this loads HTML template
        ClassLoader classLoader = getClass().getClassLoader();
        String htmlContent = new String(classLoader.getResourceAsStream(templatePath).readAllBytes());

        // Replace template values
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            htmlContent = htmlContent.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    //this resends the verification otp for user to verify/activate account
    @Async
    public void resendOtpEmail(String to, String subject, String templateName, Map<String, String> variables) throws MessagingException, IOException {

        String templatePath = "Template/" + templateName;

        // this loads HTML template
        ClassLoader classLoader = getClass().getClassLoader();
        String htmlContent = new String(classLoader.getResourceAsStream(templatePath).readAllBytes());

        // Replace template values
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            htmlContent = htmlContent.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
