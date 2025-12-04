package Wandera.E_Commerce.App.Services.ServiceImpl;

import Wandera.E_Commerce.App.Dtos.ProfileRequest;
import Wandera.E_Commerce.App.Dtos.ProfileResponse;
import Wandera.E_Commerce.App.Entities.Role;
import Wandera.E_Commerce.App.Entities.UserEntity;
import Wandera.E_Commerce.App.Mapper.ProfileMapper;
import Wandera.E_Commerce.App.Repositories.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserEntityImplementation {

    private final PasswordEncoder passwordEncoder;
    private final UserEntityRepository userEntityRepository;

    @CacheEvict(value = "ProfileResponse",allEntries = true)
    @Cacheable(value = "ProfileResponse", key = "#profileRequest.getEmail()") //cache
    public ProfileResponse registerUser(ProfileRequest profileRequest) {
        Optional<UserEntity> existingUser = userEntityRepository.findByEmail(profileRequest.getEmail());

        if (existingUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        Role assignedRole;

        // If role is not provided → automatically assign it to USER
        if (profileRequest.getRole() == null) {
            assignedRole = Role.USER;
        }
        // If role is provided → accept it
        else {
            assignedRole = Role.valueOf(profileRequest.getRole());
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(UUID.randomUUID().toString());
        userEntity.setFirstName(profileRequest.getFirstName());
        userEntity.setLastName(profileRequest.getLastName());
        userEntity.setEmail(profileRequest.getEmail());
        userEntity.setPassword(profileRequest.getPassword());
        userEntity.setEmail(profileRequest.getEmail());
        userEntity.setPassword(passwordEncoder.encode(profileRequest.getPassword()));
        userEntity.setCountry(profileRequest.getCountry());
        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity.setUpdatedAt(LocalDateTime.now());
        userEntity.setPhoneNumber(profileRequest.getPhoneNumber());
        userEntity.setRole(assignedRole);

        var savedUser= userEntityRepository.save(userEntity);


       return ProfileMapper.toDto(savedUser);
    }

    public UserEntity getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() ||
                auth.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        String email = auth.getName();
        return userEntityRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}
