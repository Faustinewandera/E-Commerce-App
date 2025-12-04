package Wandera.E_Commerce.App.Controllers;

import Wandera.E_Commerce.App.Dtos.ForgetPasswordRequest;
import Wandera.E_Commerce.App.Dtos.ResetPasswordRequest;
import Wandera.E_Commerce.App.Services.ServiceImpl.PasswordResetTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class PasswordResetTokenController {
    private final PasswordResetTokenService passwordResetTokenService;

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgetPasswordRequest forgetPasswordRequest) {
        String token = passwordResetTokenService.createPasswordResetToken(forgetPasswordRequest);

        // In real system, send email — here you just return it
        return ResponseEntity.ok("Reset token: " + token);

    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {

        passwordResetTokenService.resetPassword(resetPasswordRequest);

        return ResponseEntity.ok("Password reset successfully");
    }

}
