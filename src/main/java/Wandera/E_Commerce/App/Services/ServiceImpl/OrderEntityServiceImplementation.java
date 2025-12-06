package Wandera.E_Commerce.App.Services.ServiceImpl;

import Wandera.E_Commerce.App.Dtos.OrderItemResponse;
import Wandera.E_Commerce.App.Dtos.OrderRequest;
import Wandera.E_Commerce.App.Dtos.OrderResponse;
import Wandera.E_Commerce.App.EmailConfig.EmailService;
import Wandera.E_Commerce.App.Enum.PaymentStatus;
import Wandera.E_Commerce.App.EmailConfig.Notification;
import Wandera.E_Commerce.App.Entities.*;
import Wandera.E_Commerce.App.Repositories.CartRepository;
import Wandera.E_Commerce.App.EmailConfig.NotificationRepository;
import Wandera.E_Commerce.App.Repositories.OrderEntityRepository;
import Wandera.E_Commerce.App.Repositories.OrderItemRepository;
import Wandera.E_Commerce.App.Services.Interfaces.OrderEntityInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest orderRequest) {

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

                    // ✔ Notify seller for each product sold
                    SellerProfile seller = cartItem.getProduct().getSeller();
                    if (seller != null) {

                        String message =
                                "New order received!\n" +
                                        "Product: " + cartItem.getProduct().getProductName() + "\n" +
                                        "Quantity: " + cartItem.getQuantity() + "\n" +
                                        "SubTotal: " + cartItem.getSubTotal() +"\n" +
                                        "Customer Email: " + user.getEmail() + "\n" +
                                        "Phone: " + user.getPhoneNumber() + "\n" +
                                        "Location: " + user.getCountry();

                        // ✔ sending email
                        emailService.sendEmailToSeller(
                                seller.getEmail(),
                                "You made a sale!",
                                message
                        );

                        // ✔ save notification
                        Notification notification = Notification.builder()
                                .user(seller.getUser())
                                .message("You have a new order for: " + cartItem.getProduct().getProductName())
                                .createdAt(LocalDateTime.now())
                                .build();

                        notificationRepository.save(notification);
                    }

                    return item;
                })
                .collect(Collectors.toList());

        // Save order amounts
        order.setItems(orderItems);
        double totalAmount = orderItems.stream().mapToDouble(OrderItem::getSubTotal).sum();
        order.setTotalAmount(BigDecimal.valueOf(totalAmount));

        // Handle payment method
        if ("CASH".equalsIgnoreCase(String.valueOf(orderRequest.getPaymentMethod()))) {
            order.setStatus(PaymentStatus.valueOf("PAID"));
        } else {
            order.setStatus(PaymentStatus.valueOf("PENDING")); // For M_PESA payment
        }

        orderRepository.save(order);

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
    public List<OrderResponse> getAllOrder() {

        List<OrderEntity> orders = orderRepository.findByOrderByCreatedAtDesc();

        return orders.stream().map(order -> {

            List<OrderItemResponse> itemResponses = order.getItems().stream()
                    .map(item -> OrderItemResponse.builder()
                            .productId(item.getProduct().getProductId())
                            .productName(item.getProduct().getProductName())
                            .quantity(item.getQuantity())
                            .subTotal(item.getSubTotal())
                            .build()
                    ).collect(Collectors.toList());

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
        UserEntity user = userService.getLoggedInUser();

        OrderEntity order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .productId(item.getProduct().getProductId())
                        .productName(item.getProduct().getProductName())
                        .quantity(item.getQuantity())
                        .subTotal(item.getSubTotal() * item.getQuantity()) // double * int
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
