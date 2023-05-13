package de.farue.autocut.repository;

import de.farue.autocut.domain.TransactionBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the TransactionBook entity.
 */
@Repository
public interface TransactionBookRepository extends JpaRepository<TransactionBook, Long> {}
