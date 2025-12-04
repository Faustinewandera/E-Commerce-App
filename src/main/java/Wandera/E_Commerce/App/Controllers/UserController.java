package Wandera.E_Commerce.App.Controllers;

import Wandera.E_Commerce.App.Dtos.ProfileRequest;
import Wandera.E_Commerce.App.Dtos.ProfileResponse;
import Wandera.E_Commerce.App.Services.ServiceImpl.UserEntityImplementation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserEntityImplementation userEntityImplementation;

    @PostMapping("/register")
    public ProfileResponse registerUser(@Valid @RequestBody ProfileRequest profileRequest) {
        return userEntityImplementation.registerUser(profileRequest);
    }

}
