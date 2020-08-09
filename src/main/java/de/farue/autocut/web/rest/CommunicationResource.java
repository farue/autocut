package de.farue.autocut.web.rest;

import de.farue.autocut.domain.Communication;
import de.farue.autocut.repository.CommunicationRepository;
import de.farue.autocut.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link de.farue.autocut.domain.Communication}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CommunicationResource {

    private final Logger log = LoggerFactory.getLogger(CommunicationResource.class);

    private static final String ENTITY_NAME = "communication";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CommunicationRepository communicationRepository;

    public CommunicationResource(CommunicationRepository communicationRepository) {
        this.communicationRepository = communicationRepository;
    }

    /**
     * {@code POST  /communications} : Create a new communication.
     *
     * @param communication the communication to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new communication, or with status {@code 400 (Bad Request)} if the communication has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/communications")
    public ResponseEntity<Communication> createCommunication(@Valid @RequestBody Communication communication) throws URISyntaxException {
        log.debug("REST request to save Communication : {}", communication);
        if (communication.getId() != null) {
            throw new BadRequestAlertException("A new communication cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Communication result = communicationRepository.save(communication);
        return ResponseEntity.created(new URI("/api/communications/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /communications} : Updates an existing communication.
     *
     * @param communication the communication to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated communication,
     * or with status {@code 400 (Bad Request)} if the communication is not valid,
     * or with status {@code 500 (Internal Server Error)} if the communication couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/communications")
    public ResponseEntity<Communication> updateCommunication(@Valid @RequestBody Communication communication) throws URISyntaxException {
        log.debug("REST request to update Communication : {}", communication);
        if (communication.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Communication result = communicationRepository.save(communication);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, communication.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /communications} : get all the communications.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of communications in body.
     */
    @GetMapping("/communications")
    public List<Communication> getAllCommunications() {
        log.debug("REST request to get all Communications");
        return communicationRepository.findAll();
    }

    /**
     * {@code GET  /communications/:id} : get the "id" communication.
     *
     * @param id the id of the communication to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the communication, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/communications/{id}")
    public ResponseEntity<Communication> getCommunication(@PathVariable Long id) {
        log.debug("REST request to get Communication : {}", id);
        Optional<Communication> communication = communicationRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(communication);
    }

    /**
     * {@code DELETE  /communications/:id} : delete the "id" communication.
     *
     * @param id the id of the communication to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/communications/{id}")
    public ResponseEntity<Void> deleteCommunication(@PathVariable Long id) {
        log.debug("REST request to delete Communication : {}", id);
        communicationRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
