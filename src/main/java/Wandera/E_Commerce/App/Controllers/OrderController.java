package Wandera.E_Commerce.App.Controllers;

import Wandera.E_Commerce.App.Dtos.OrderRequest;
import Wandera.E_Commerce.App.Dtos.OrderResponse;
import Wandera.E_Commerce.App.Services.ServiceImpl.OrderEntityServiceImplementation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderEntityServiceImplementation orderEntityServiceImplementation;

    @PostMapping("/place")
    public OrderResponse placeOrder(@RequestBody OrderRequest orderRequest) {
        return orderEntityServiceImplementation.placeOrder(orderRequest);
    }
}
