package Wandera.E_Commerce.App.EmailConfig;

import Wandera.E_Commerce.App.Dtos.ResetPasswordRequest;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class PasswordResetTokenController {
    private final PasswordResetTokenService passwordResetTokenService;

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgetPasswordRequest forgetPasswordRequest) throws MessagingException, IOException {
        passwordResetTokenService.createPasswordResetToken(forgetPasswordRequest);
        return ResponseEntity.ok("OTP sent to your email ");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {

        passwordResetTokenService.resetPassword(resetPasswordRequest);

        return ResponseEntity.ok("Password reset successfully");
    }

}
