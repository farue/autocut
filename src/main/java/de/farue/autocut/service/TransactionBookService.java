package de.farue.autocut.service;

import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.repository.TransactionBookRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link TransactionBook}.
 */
@Service
@Transactional
public class TransactionBookService {

    private final Logger log = LoggerFactory.getLogger(TransactionBookService.class);

    private final TransactionBookRepository transactionBookRepository;

    public TransactionBookService(TransactionBookRepository transactionBookRepository) {
        this.transactionBookRepository = transactionBookRepository;
    }

    /**
     * Save a transactionBook.
     *
     * @param transactionBook the entity to save.
     * @return the persisted entity.
     */
    public TransactionBook save(TransactionBook transactionBook) {
        log.debug("Request to save TransactionBook : {}", transactionBook);
        return transactionBookRepository.save(transactionBook);
    }

    /**
     * Partially update a transactionBook.
     *
     * @param transactionBook the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TransactionBook> partialUpdate(TransactionBook transactionBook) {
        log.debug("Request to partially update TransactionBook : {}", transactionBook);

        return transactionBookRepository
            .findById(transactionBook.getId())
            .map(
                existingTransactionBook -> {
                    if (transactionBook.getName() != null) {
                        existingTransactionBook.setName(transactionBook.getName());
                    }
                    if (transactionBook.getType() != null) {
                        existingTransactionBook.setType(transactionBook.getType());
                    }

                    return existingTransactionBook;
                }
            )
            .map(transactionBookRepository::save);
    }

    /**
     * Get all the transactionBooks.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<TransactionBook> findAll() {
        log.debug("Request to get all TransactionBooks");
        return transactionBookRepository.findAll();
    }

    /**
     * Get one transactionBook by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TransactionBook> findOne(Long id) {
        log.debug("Request to get TransactionBook : {}", id);
        return transactionBookRepository.findById(id);
    }

    /**
     * Delete the transactionBook by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete TransactionBook : {}", id);
        transactionBookRepository.deleteById(id);
    }
}
