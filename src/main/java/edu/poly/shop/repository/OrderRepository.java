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

import edu.poly.shop.domain.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByOrderDateEquals(Date orderDate, Pageable pageable);

    Page<Order> findAll(Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.customer.customerId = :customerId AND o.status = 0")
    Optional<Order> findByOrderByCustomer(@Param("customerId") Long customerId);

    @Query("SELECT o FROM Order o WHERE o.customer.customerId = :customerId AND o.status = 1")
    List<Order> findAllByCustomerId(@Param("customerId") Long customerId);

    // report

    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    List<Order> findOrdersBetweenDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT o FROM Order o WHERE o.orderDate >= :startDate")
    List<Order> findOrdersFrom(@Param("startDate") Date startDate);

    @Query("SELECT od.product.category.name, SUM(od.price * od.quantity) FROM OrderDetail od WHERE od.order.orderDate BETWEEN :startDate AND :endDate GROUP BY od.product.category.name")
    List<Object[]> findRevenueByCategoryBetweenDates(@Param("startDate") Date startDate,
            @Param("endDate") Date endDate);
}
