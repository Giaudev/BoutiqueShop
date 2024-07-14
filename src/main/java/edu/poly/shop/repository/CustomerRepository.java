package edu.poly.shop.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.poly.shop.domain.Account;
import edu.poly.shop.domain.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByFullnameContaining(String fullname);

    Page<Customer> findByFullnameContaining(String fullname, Pageable pageable);

    Page<Customer> findAll(Pageable pageable);

    @Query("SELECT o FROM Customer o WHERE o.account.accountId = :accountId")
    Customer findByAccountId(@Param("accountId") Long accountId);

    // hiện thị thông tin khách hàng ở phần checkout
    @Query("SELECT  c FROM Customer c WHERE c.customerId = :customerId")
    Customer findByCustomerByCustomerId(@Param("customerId") Long customerId);
}
