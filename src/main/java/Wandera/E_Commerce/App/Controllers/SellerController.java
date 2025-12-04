package Wandera.E_Commerce.App.Controllers;

import Wandera.E_Commerce.App.Dtos.OrderResponse;
import Wandera.E_Commerce.App.Dtos.SellerProfileRequest;
import Wandera.E_Commerce.App.Dtos.SellerResponse;
import Wandera.E_Commerce.App.Services.ServiceImpl.OrderEntityServiceImplementation;
import Wandera.E_Commerce.App.Services.ServiceImpl.SellerProfileImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seller")
public class SellerController {

    private final SellerProfileImpl sellerProfileImpl;
    private final OrderEntityServiceImplementation orderEntityServiceImplementation;

    @PostMapping("/addSellerProfile")
    public SellerResponse addSellerProfile(@RequestBody SellerProfileRequest sellerProfileRequest) {
        return sellerProfileImpl.addSellerProfile(sellerProfileRequest);
    }
    @GetMapping("/getAllOrder")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<OrderResponse> getAllOrder() {
        return orderEntityServiceImplementation.getAllOrder();
    }

    @GetMapping("/getByOrderId")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OrderResponse findByOrderId(@PathVariable String orderNumber) {
        return orderEntityServiceImplementation.getByOrderId(orderNumber);
    }
}
