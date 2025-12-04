package Wandera.E_Commerce.App.Services.Interfaces;

import Wandera.E_Commerce.App.Dtos.CartRequest;
import Wandera.E_Commerce.App.Dtos.CartResponse;

public interface CartInterface {
    CartResponse addToCart(CartRequest request);

    void deleteCartItem(Long id);
}
