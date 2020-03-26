package de.farue.autocut.web.rest;

import de.farue.autocut.domain.LaundryMachineProgram;
import de.farue.autocut.repository.LaundryMachineProgramRepository;
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
 * REST controller for managing {@link de.farue.autocut.domain.LaundryMachineProgram}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class LaundryMachineProgramResource {

    private final Logger log = LoggerFactory.getLogger(LaundryMachineProgramResource.class);

    private static final String ENTITY_NAME = "laundryMachineProgram";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LaundryMachineProgramRepository laundryMachineProgramRepository;

    public LaundryMachineProgramResource(LaundryMachineProgramRepository laundryMachineProgramRepository) {
        this.laundryMachineProgramRepository = laundryMachineProgramRepository;
    }

    /**
     * {@code POST  /laundry-machine-programs} : Create a new laundryMachineProgram.
     *
     * @param laundryMachineProgram the laundryMachineProgram to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new laundryMachineProgram, or with status {@code 400 (Bad Request)} if the laundryMachineProgram has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/laundry-machine-programs")
    public ResponseEntity<LaundryMachineProgram> createLaundryMachineProgram(@Valid @RequestBody LaundryMachineProgram laundryMachineProgram) throws URISyntaxException {
        log.debug("REST request to save LaundryMachineProgram : {}", laundryMachineProgram);
        if (laundryMachineProgram.getId() != null) {
            throw new BadRequestAlertException("A new laundryMachineProgram cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LaundryMachineProgram result = laundryMachineProgramRepository.save(laundryMachineProgram);
        return ResponseEntity.created(new URI("/api/laundry-machine-programs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /laundry-machine-programs} : Updates an existing laundryMachineProgram.
     *
     * @param laundryMachineProgram the laundryMachineProgram to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated laundryMachineProgram,
     * or with status {@code 400 (Bad Request)} if the laundryMachineProgram is not valid,
     * or with status {@code 500 (Internal Server Error)} if the laundryMachineProgram couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/laundry-machine-programs")
    public ResponseEntity<LaundryMachineProgram> updateLaundryMachineProgram(@Valid @RequestBody LaundryMachineProgram laundryMachineProgram) throws URISyntaxException {
        log.debug("REST request to update LaundryMachineProgram : {}", laundryMachineProgram);
        if (laundryMachineProgram.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        LaundryMachineProgram result = laundryMachineProgramRepository.save(laundryMachineProgram);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, laundryMachineProgram.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /laundry-machine-programs} : get all the laundryMachinePrograms.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of laundryMachinePrograms in body.
     */
    @GetMapping("/laundry-machine-programs")
    public List<LaundryMachineProgram> getAllLaundryMachinePrograms() {
        log.debug("REST request to get all LaundryMachinePrograms");
        return laundryMachineProgramRepository.findAll();
    }

    /**
     * {@code GET  /laundry-machine-programs/:id} : get the "id" laundryMachineProgram.
     *
     * @param id the id of the laundryMachineProgram to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the laundryMachineProgram, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/laundry-machine-programs/{id}")
    public ResponseEntity<LaundryMachineProgram> getLaundryMachineProgram(@PathVariable Long id) {
        log.debug("REST request to get LaundryMachineProgram : {}", id);
        Optional<LaundryMachineProgram> laundryMachineProgram = laundryMachineProgramRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(laundryMachineProgram);
    }

    /**
     * {@code DELETE  /laundry-machine-programs/:id} : delete the "id" laundryMachineProgram.
     *
     * @param id the id of the laundryMachineProgram to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/laundry-machine-programs/{id}")
    public ResponseEntity<Void> deleteLaundryMachineProgram(@PathVariable Long id) {
        log.debug("REST request to delete LaundryMachineProgram : {}", id);
        laundryMachineProgramRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
