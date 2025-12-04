package Wandera.E_Commerce.App.Controllers;

import Wandera.E_Commerce.App.Confi.AppUserDetailService;
import Wandera.E_Commerce.App.Dtos.LoginRequest;
import Wandera.E_Commerce.App.Dtos.LoginResponse;
import Wandera.E_Commerce.App.Jwt.JwtService;
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

        private final AuthenticationManager authenticationManager;
        private final AppUserDetailService userDetailService;
        private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
        @Cacheable(value = "LoginResponse", key = "#loginRequest.getEmail()")
        public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
            log.info("logged in:{}", loginRequest);
            try{
                authenticate(loginRequest.getEmail(),loginRequest.getPassword());
                final UserDetails userDetails=userDetailService.loadUserByUsername(loginRequest.getEmail());
                String jwtToken=jwtService.generateToken(userDetails);
                ResponseCookie cookie = ResponseCookie.from("jwt", jwtToken)
                        .httpOnly(true)
                        .secure(true)
                        .path("/")
                        .maxAge(Duration.ofDays(1))
                        .sameSite("strict")
                        .build();
                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString())
                        .body(new LoginResponse(loginRequest.getEmail(),jwtToken));


            } catch (BadCredentialsException ex) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", true);
                response.put("message", "Incorrect email or password");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( response);

            }
            catch (DisabledException ex) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", true);
                response.put("message", "account is disabled");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body( response);

            }
            catch (Exception e) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", true);
                response.put("message", "Authentication Failed");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body( response);

            }
        }

        private void authenticate(String email, String password) {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        }

        @PostMapping("/encode")
        public String adminPasswordEncoder(@RequestBody Map<String, String> request) {
            return passwordEncoder.encode(request.get("password"));
        }

}


