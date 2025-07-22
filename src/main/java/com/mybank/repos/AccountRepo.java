package com.mybank.repos;

import com.mybank.entities.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepo extends JpaRepository<Accounts, Long> {
    List<Accounts> findByUserUserId(Long userId);
    List<Accounts> findByAccountType(String accountType);
    List<Accounts> findByBalanceAmountGreaterThan(Double amount);
    @Query("SELECT COUNT(a) FROM Accounts a WHERE a.user.userId = :userId")
    Long countAccountsByUserId(@Param("userId") Long userId);
}
