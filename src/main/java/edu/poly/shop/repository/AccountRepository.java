package edu.poly.shop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.poly.shop.domain.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUsernameContaining(String username);

    Page<Account> findByUsernameContaining(String username, Pageable pageable);

    Page<Account> findAll(Pageable pageable);

    Account findByUsernameEquals(String username);

    Optional<Account> findByUsername(String username);
}
