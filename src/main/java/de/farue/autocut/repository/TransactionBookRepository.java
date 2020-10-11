package de.farue.autocut.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.enumeration.TransactionBookType;

/**
 * Spring Data  repository for the TransactionBook entity.
 */
@Repository
public interface TransactionBookRepository extends JpaRepository<TransactionBook, Long> {

    Optional<TransactionBook> findOneByNameAndType(String name, TransactionBookType type);
}
