package Wandera.E_Commerce.App.Services.ServiceImpl;

import Wandera.E_Commerce.App.Dtos.SellerProfileRequest;
import Wandera.E_Commerce.App.Dtos.SellerResponse;
import Wandera.E_Commerce.App.Enum.Role;
import Wandera.E_Commerce.App.Entities.SellerProfile;
import Wandera.E_Commerce.App.Entities.UserEntity;
import Wandera.E_Commerce.App.Mapper.SellerMapper;
import Wandera.E_Commerce.App.Repositories.SellerProfileRepository;
import Wandera.E_Commerce.App.Repositories.UserEntityRepository;
import Wandera.E_Commerce.App.Services.Interfaces.SellerProfileInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SellerProfileImpl implements SellerProfileInterface {

    private final SellerProfileRepository sellerProfileRepository;
    private final UserEntityRepository userEntityRepository;

    @Override
    public SellerResponse addSellerProfile(SellerProfileRequest sellerProfileRequest) {

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user=userEntityRepository.findByEmail(userEmail)
                .orElseThrow(()->new RuntimeException("User not found"));


        if (!user.getRole().equals(Role.SELLER)) {
            throw new RuntimeException(" Not registered as a Seller");
        }

        if (user.getSellerProfile()!=null) {
            throw new RuntimeException(" SellerProfile already exists");
        }

        // 5️⃣ Save seller profile
        SellerProfile profile = new SellerProfile();
        profile.setSellerId(UUID.randomUUID().toString());
        profile.setStoreName(sellerProfileRequest.getStoreName());
        profile.setPhoneNumber(sellerProfileRequest.getPhoneNumber());
        profile.setStoreAddress(sellerProfileRequest.getStoreAddress());
        profile.setIdNumber(String.valueOf(sellerProfileRequest.getIdNumber()));
        profile.setEmail(user.getEmail());
        profile.setAccountNumber(sellerProfileRequest.getAccountNumber());

        profile.setUser(user);

        var savedEntity=sellerProfileRepository.save(profile);

      return SellerMapper.toDto(savedEntity);
    }

}
