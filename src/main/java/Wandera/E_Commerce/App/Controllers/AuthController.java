package Wandera.E_Commerce.App.Controllers;

import Wandera.E_Commerce.App.Entities.UserEntity;
import Wandera.E_Commerce.App.Repositories.UserEntityRepository;
import Wandera.E_Commerce.App.SecurityConfiguration.AppUserDetailService;
import Wandera.E_Commerce.App.Dtos.LoginRequest;
import Wandera.E_Commerce.App.Dtos.LoginResponse;
import Wandera.E_Commerce.App.Jwt.JwtService;
import Wandera.E_Commerce.App.Services.ServiceImpl.LoginServiceImplementation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

        private final PasswordEncoder passwordEncoder;
        private final LoginServiceImplementation loginServiceImplementation;

       @PostMapping("/login")
        @Cacheable(value = "LoginResponse", key = "#loginRequest.getEmail()")
        public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

           return loginServiceImplementation.login(loginRequest);

       }
        @PostMapping("/encode")
        public String adminPasswordEncoder(@RequestBody Map<String, String> request) {
            return passwordEncoder.encode(request.get("password"));
        }

}


