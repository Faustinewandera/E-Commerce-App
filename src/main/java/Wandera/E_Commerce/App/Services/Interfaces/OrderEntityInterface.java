package Wandera.E_Commerce.App.Services.Interfaces;


import Wandera.E_Commerce.App.Dtos.OrderRequest;
import Wandera.E_Commerce.App.Dtos.OrderResponse;

import java.util.List;

public interface OrderEntityInterface {
    OrderResponse placeOrder(OrderRequest orderRequest);

    List<OrderResponse> getAllOrder();

    OrderResponse getByOrderId(String orderNumber);
}
