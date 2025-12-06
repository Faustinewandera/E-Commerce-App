package Wandera.E_Commerce.App.Services.ServiceImpl;

import Wandera.E_Commerce.App.EmailConfig.EmailService;
import Wandera.E_Commerce.App.Entities.OtpVerification;
import Wandera.E_Commerce.App.Entities.UserEntity;
import Wandera.E_Commerce.App.Repositories.OtpVerificationRepository;
import Wandera.E_Commerce.App.Repositories.UserEntityRepository;
import Wandera.E_Commerce.App.Services.Interfaces.OtpVerificationServiceInterface;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpVerificationServiceImplementation implements OtpVerificationServiceInterface {

     private final OtpVerificationRepository otpVerificationRepository;
     private final EmailService emailService;
     private final UserEntityRepository userEntityRepository;

    // Generate and send OTP for registered user
    public void verifyEmailAdress(UserEntity user) throws MessagingException, IOException {

        // This  Generate OTP
        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);

        // 2. Save OTP

        OtpVerification token = new OtpVerification();
        token.setOtpCode(otp);
        token.setUser(user);
        token.setExpiryTime(LocalDateTime.now().plusMinutes(10));

        otpVerificationRepository.save(token);

        // This sends  OTP email to registered user
        // Prepare template variables
        Map<String, String> variables = new HashMap<>();
        variables.put("otp", (otp));
        variables.put("username", user.getFirstName() );

        emailService.sendOtpRegisterVerification(
                user.getEmail(),
                "Your Account Verification OTP",
                "OtpVerification.html",
                variables
        );
    }


    //verify user account from false to true
    public void verifyEmailByAOtp(String otpCode) {

        // 1. Find token
        OtpVerification token = otpVerificationRepository.findByOtpCode(String.valueOf(otpCode))
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

        // Checks the  expiration time
        if (token.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }


        UserEntity user = token.getUser();

        // 4. Verify user
        user.setVerified(true);
        userEntityRepository.save(user);

        // this deletes OTP after success
        otpVerificationRepository.delete(token);
    }

    @Override
    public void resendCodeToken(String otpCode) throws MessagingException, IOException {
        OtpVerification otpEntity = otpVerificationRepository.findByOtpCode(otpCode)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        UserEntity user = otpEntity.getUser();

        if (user.isVerified()) {
            throw new RuntimeException("Account already verified");
        }

        // delete old otp
        otpVerificationRepository.delete(otpEntity);

        // generate new otp
        String otp = String.format("%06d", new Random().nextInt(999999));

        OtpVerification newOtp = new OtpVerification();
        newOtp.setOtpCode(otp);
        newOtp.setUser(user);
        newOtp.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        otpVerificationRepository.save(newOtp);

        // This sends  OTP email to registered user
        // Prepare template variables
        Map<String, String> variables = new HashMap<>();
        variables.put("username", user.getFirstName() );
        variables.put("otp", (otp));

        emailService.resendOtpEmail(
                user.getEmail(),
                "Your Password Reset Code",
                "OtpVerification.html",
                variables
        );

    }
}
