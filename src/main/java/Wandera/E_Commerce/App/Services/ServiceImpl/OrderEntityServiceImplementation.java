package Wandera.E_Commerce.App.Services.ServiceImpl;

import Wandera.E_Commerce.App.Dtos.OrderItemResponse;
import Wandera.E_Commerce.App.Dtos.OrderRequest;
import Wandera.E_Commerce.App.Dtos.OrderResponse;
import Wandera.E_Commerce.App.Enum.PaymentStatus;
import Wandera.E_Commerce.App.Entities.*;
import Wandera.E_Commerce.App.Repositories.CartRepository;
import Wandera.E_Commerce.App.Repositories.OrderEntityRepository;
import Wandera.E_Commerce.App.Repositories.OrderItemRepository;
import Wandera.E_Commerce.App.Services.Interfaces.OrderEntityInterface;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderEntityServiceImplementation implements OrderEntityInterface {

    private final UserEntityImplementation userService;
    private final OrderEntityRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final NotificationServiceImplementation notificationService;

    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest orderRequest) throws MessagingException, IOException {

        UserEntity user = userService.getLoggedInUser();
        Cart cart = user.getCart();

        if (cart == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Create order
        OrderEntity order = OrderEntity.builder()
                .customer(user)
                .paymentMethod(orderRequest.getPaymentMethod())
                .createdAt(LocalDateTime.now())
                .build();

        order.generateMetadata();

        // Convert CartItems → OrderItems
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {

                    OrderItem item = OrderItem.builder()
                            .order(order)
                            .product(cartItem.getProduct())
                            .quantity(cartItem.getQuantity())
                            .subTotal(cartItem.getSubTotal())
                            .build();

                    orderItemRepository.save(item);

                    // 🔔 Notify seller (delegated)
                    try {
                        notificationService.notifySellerOnOrder(
                                cartItem.getProduct().getSeller(),
                                item,
                                user
                        );
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    return item;
                })
                .collect(Collectors.toList());

        // Save order totals
        order.setItems(orderItems);
        double totalAmount = orderItems.stream()
                .mapToDouble(OrderItem::getSubTotal)
                .sum();
        order.setTotalAmount(BigDecimal.valueOf(totalAmount));

        // Payment status
        if ("CASH".equalsIgnoreCase(String.valueOf(orderRequest.getPaymentMethod()))) {
            order.setStatus(PaymentStatus.PAID);
        } else {
            order.setStatus(PaymentStatus.PENDING);
        }

        orderRepository.save(order);

// 🔔 Notify buyer
        notificationService.notifyBuyerOnOrderPlaced(user, order);


        // Clear cart
        cart.getItems().clear();
        cart.setTotalAmount(0);
        cartRepository.save(cart);

        // Build response
        List<OrderItemResponse> itemResponses = orderItems.stream()
                .map(i -> OrderItemResponse.builder()
                        .productId(i.getProduct().getProductId())
                        .productName(i.getProduct().getProductName())
                        .quantity(i.getQuantity())
                        .subTotal(i.getSubTotal())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .orderItems(itemResponses)
                .build();
    }

    @Override
    public List<OrderResponse> getAllOrder(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<OrderEntity> orders = orderRepository.findByOrderByCreatedAtDesc(pageable);

        return orders.stream().map(order -> {

            List<OrderItemResponse> itemResponses = order.getItems().stream()
                    .map(item -> OrderItemResponse.builder()
                            .productId(item.getProduct().getProductId())
                            .productName(item.getProduct().getProductName())
                            .quantity(item.getQuantity())
                            .subTotal(item.getSubTotal())
                            .build())
                    .collect(Collectors.toList());

            return OrderResponse.builder()
                    .id(order.getId())
                    .totalAmount(order.getTotalAmount())
                    .createdAt(order.getCreatedAt())
                    .orderItems(itemResponses)
                    .build();

        }).collect(Collectors.toList());
    }

    @Override
    public OrderResponse getByOrderId(String orderNumber) {

        OrderEntity order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .productId(item.getProduct().getProductId())
                        .productName(item.getProduct().getProductName())
                        .quantity(item.getQuantity())
                        .subTotal(item.getSubTotal() * item.getQuantity())
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .orderItems(itemResponses)
                .build();
    }
}
