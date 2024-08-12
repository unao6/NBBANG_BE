package com.elice.nbbang.domain.payment.repository;

import com.elice.nbbang.domain.payment.entity.Account;
import com.elice.nbbang.domain.payment.entity.enums.AccountType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByUserId(Long userId);

    Optional<Account> findByAccountType(AccountType accountType);

    Account save(Account account);

    void delete(Account account);
}
