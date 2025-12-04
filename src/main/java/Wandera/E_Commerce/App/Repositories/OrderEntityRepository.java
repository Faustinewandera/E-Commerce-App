package Wandera.E_Commerce.App.Repositories;

import Wandera.E_Commerce.App.Entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderEntityRepository extends JpaRepository<OrderEntity,Long> {
    List<OrderEntity> findByOrderByCreatedAtDesc();
    Optional<OrderEntity> findByOrderNumber(String orderNumber);

}
