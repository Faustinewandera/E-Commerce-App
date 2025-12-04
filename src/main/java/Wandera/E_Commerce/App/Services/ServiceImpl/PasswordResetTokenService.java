package Wandera.E_Commerce.App.Services.ServiceImpl;

import Wandera.E_Commerce.App.Dtos.ForgetPasswordRequest;
import Wandera.E_Commerce.App.Dtos.ResetPasswordRequest;
import Wandera.E_Commerce.App.Entities.PasswordResetToken;
import Wandera.E_Commerce.App.Entities.UserEntity;
import Wandera.E_Commerce.App.Exceptions.BadRequestException;
import Wandera.E_Commerce.App.Exceptions.ResourceNotFoundException;
import Wandera.E_Commerce.App.Repositories.PasswordResetTokenRepository;
import Wandera.E_Commerce.App.Repositories.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserEntityRepository  userRepository;
    private final PasswordEncoder passwordEncoder;

    public String createPasswordResetToken(ForgetPasswordRequest forgetPasswordRequest) {

        UserEntity user = userRepository.findByEmail(forgetPasswordRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Email not found"));

        String token = UUID.randomUUID().toString();

        PasswordResetToken reset = new PasswordResetToken();
        reset.setToken(token);
        reset.setUser(user);
        reset.setExpiration(LocalDateTime.now().plusMinutes(15));

        passwordResetTokenRepository.save(reset);

        return token;

    }

    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        PasswordResetToken reset = passwordResetTokenRepository.findByToken(resetPasswordRequest.getToken())
                .orElseThrow(() -> new BadRequestException("Invalid token"));

        if (reset.getExpiration().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Token has expired");
        }

        UserEntity user = reset.getUser();
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));

        userRepository.save(user);
        passwordResetTokenRepository.delete(reset);

    }
}
