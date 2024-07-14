package edu.poly.shop.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.poly.shop.domain.Account;
import edu.poly.shop.domain.CartItem;
import edu.poly.shop.domain.Order;
import edu.poly.shop.domain.OrderDetail;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

        Page<OrderDetail> findByOrderOrderId(Long orderId, Pageable pageable);

        Page<OrderDetail> findAll(Pageable pageable);

        @Query("SELECT o FROM OrderDetail o WHERE o.order.orderId = :orderId AND o.product.productId = :productId")
        Optional<OrderDetail> findOneByOrderidAndProductid(@Param("orderId") Long orderId,
                        @Param("productId") Long productId);

        List<OrderDetail> findByOrderOrderId(Long orderId);

        @Query("SELECT new edu.poly.shop.domain.CartItem(p.productId,p.image, p.name, od.price, od.quantity, o.amount) "
                        +
                        "FROM OrderDetail od " +
                        "JOIN od.order o " +
                        "JOIN od.product p " +
                        "WHERE o.orderId = :orderId")
        List<CartItem> findOrderProductDetailsByOrderId(@Param("orderId") Long orderId);

        @Query("SELECT od FROM OrderDetail od WHERE od.order.customer.customerId = :customerId AND od.product.productId = :productId")
        List<OrderDetail> findByOrderIdAndProductId(@Param("customerId") Long customerId,
                        @Param("productId") Long productId);

        @Query("SELECT new edu.poly.shop.domain.CartItem(p.name,od.price,od.quantity) FROM OrderDetail od JOIN od.order o JOIN od.product p WHERE o.customer.customerId = :customerId")
        List<CartItem> findOrderProductDetailsByCustomerId(@Param("customerId") Long customerId);
}
