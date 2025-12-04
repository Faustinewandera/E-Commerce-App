package Wandera.E_Commerce.App.Controllers;

import Wandera.E_Commerce.App.Dtos.CartRequest;
import Wandera.E_Commerce.App.Dtos.CartResponse;
import Wandera.E_Commerce.App.Services.ServiceImpl.CartServiceImplementation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CartController {
    private final CartServiceImplementation cartServiceImplementation;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public CartResponse addToCart(@RequestBody CartRequest request) {
        return cartServiceImplementation.addToCart(request);
    }

    @DeleteMapping("/deleteItem/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteCartItem(@PathVariable Long id) {
        cartServiceImplementation.deleteCartItem(id);
        return ResponseEntity.ok("Cart item deleted successfully");
    }

}
