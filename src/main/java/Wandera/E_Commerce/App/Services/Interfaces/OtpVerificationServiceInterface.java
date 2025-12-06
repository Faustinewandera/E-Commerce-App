package Wandera.E_Commerce.App.Services.Interfaces;

import Wandera.E_Commerce.App.Entities.UserEntity;
import jakarta.mail.MessagingException;

import java.io.IOException;

public interface OtpVerificationServiceInterface {
    void verifyEmailAdress(UserEntity user) throws MessagingException, IOException;
    void verifyEmailByAOtp(String otpCode);

    void resendCodeToken(String otpCode) throws MessagingException, IOException;
}
