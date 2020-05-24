package de.farue.autocut.web.rest;

import de.farue.autocut.domain.LaundryMachine;
import de.farue.autocut.repository.LaundryMachineRepository;
import de.farue.autocut.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link de.farue.autocut.domain.LaundryMachine}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class LaundryMachineResource {

    private final Logger log = LoggerFactory.getLogger(LaundryMachineResource.class);

    private static final String ENTITY_NAME = "laundryMachine";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LaundryMachineRepository laundryMachineRepository;

    public LaundryMachineResource(LaundryMachineRepository laundryMachineRepository) {
        this.laundryMachineRepository = laundryMachineRepository;
    }

    /**
     * {@code POST  /laundry-machines} : Create a new laundryMachine.
     *
     * @param laundryMachine the laundryMachine to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new laundryMachine, or with status {@code 400 (Bad Request)} if the laundryMachine has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/laundry-machines")
    public ResponseEntity<LaundryMachine> createLaundryMachine(@Valid @RequestBody LaundryMachine laundryMachine) throws URISyntaxException {
        log.debug("REST request to save LaundryMachine : {}", laundryMachine);
        if (laundryMachine.getId() != null) {
            throw new BadRequestAlertException("A new laundryMachine cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LaundryMachine result = laundryMachineRepository.save(laundryMachine);
        return ResponseEntity.created(new URI("/api/laundry-machines/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /laundry-machines} : Updates an existing laundryMachine.
     *
     * @param laundryMachine the laundryMachine to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated laundryMachine,
     * or with status {@code 400 (Bad Request)} if the laundryMachine is not valid,
     * or with status {@code 500 (Internal Server Error)} if the laundryMachine couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/laundry-machines")
    public ResponseEntity<LaundryMachine> updateLaundryMachine(@Valid @RequestBody LaundryMachine laundryMachine) throws URISyntaxException {
        log.debug("REST request to update LaundryMachine : {}", laundryMachine);
        if (laundryMachine.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        LaundryMachine result = laundryMachineRepository.save(laundryMachine);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, laundryMachine.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /laundry-machines} : get all the laundryMachines.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of laundryMachines in body.
     */
    @GetMapping("/laundry-machines")
    public List<LaundryMachine> getAllLaundryMachines() {
        log.debug("REST request to get all LaundryMachines");
        return laundryMachineRepository.findAll();
    }

    /**
     * {@code GET  /laundry-machines/:id} : get the "id" laundryMachine.
     *
     * @param id the id of the laundryMachine to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the laundryMachine, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/laundry-machines/{id}")
    public ResponseEntity<LaundryMachine> getLaundryMachine(@PathVariable Long id) {
        log.debug("REST request to get LaundryMachine : {}", id);
        Optional<LaundryMachine> laundryMachine = laundryMachineRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(laundryMachine);
    }

    /**
     * {@code DELETE  /laundry-machines/:id} : delete the "id" laundryMachine.
     *
     * @param id the id of the laundryMachine to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/laundry-machines/{id}")
    public ResponseEntity<Void> deleteLaundryMachine(@PathVariable Long id) {
        log.debug("REST request to delete LaundryMachine : {}", id);

        laundryMachineRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
