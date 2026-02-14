package org.payriff.springboot.repositories;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.payriff.springboot.entities.Transaction;
import org.payriff.springboot.utilities.constants.transactionstatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Optional<Transaction> findByToken(String token);
    List<Transaction> findTop50ByPaymentProviderAndStatusInAndNextRetryAtBeforeOrderByNextRetryAtAsc(
        String provider,
        List<transactionstatus> statuses,
        Instant cutoff
    );
}
