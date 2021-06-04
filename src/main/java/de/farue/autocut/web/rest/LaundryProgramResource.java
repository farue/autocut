package de.farue.autocut.web.rest;

import de.farue.autocut.domain.LaundryProgram;
import de.farue.autocut.repository.LaundryProgramRepository;
import de.farue.autocut.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link de.farue.autocut.domain.LaundryProgram}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class LaundryProgramResource {

    private final Logger log = LoggerFactory.getLogger(LaundryProgramResource.class);

    private static final String ENTITY_NAME = "laundryProgram";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LaundryProgramRepository laundryProgramRepository;

    public LaundryProgramResource(LaundryProgramRepository laundryProgramRepository) {
        this.laundryProgramRepository = laundryProgramRepository;
    }

    /**
     * {@code POST  /laundry-programs} : Create a new laundryProgram.
     *
     * @param laundryProgram the laundryProgram to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new laundryProgram, or with status {@code 400 (Bad Request)} if the laundryProgram has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/laundry-programs")
    public ResponseEntity<LaundryProgram> createLaundryProgram(@Valid @RequestBody LaundryProgram laundryProgram)
        throws URISyntaxException {
        log.debug("REST request to save LaundryProgram : {}", laundryProgram);
        if (laundryProgram.getId() != null) {
            throw new BadRequestAlertException("A new laundryProgram cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LaundryProgram result = laundryProgramRepository.save(laundryProgram);
        return ResponseEntity
            .created(new URI("/api/laundry-programs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /laundry-programs/:id} : Updates an existing laundryProgram.
     *
     * @param id the id of the laundryProgram to save.
     * @param laundryProgram the laundryProgram to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated laundryProgram,
     * or with status {@code 400 (Bad Request)} if the laundryProgram is not valid,
     * or with status {@code 500 (Internal Server Error)} if the laundryProgram couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/laundry-programs/{id}")
    public ResponseEntity<LaundryProgram> updateLaundryProgram(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LaundryProgram laundryProgram
    ) throws URISyntaxException {
        log.debug("REST request to update LaundryProgram : {}, {}", id, laundryProgram);
        if (laundryProgram.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, laundryProgram.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!laundryProgramRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        LaundryProgram result = laundryProgramRepository.save(laundryProgram);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, laundryProgram.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /laundry-programs/:id} : Partial updates given fields of an existing laundryProgram, field will ignore if it is null
     *
     * @param id the id of the laundryProgram to save.
     * @param laundryProgram the laundryProgram to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated laundryProgram,
     * or with status {@code 400 (Bad Request)} if the laundryProgram is not valid,
     * or with status {@code 404 (Not Found)} if the laundryProgram is not found,
     * or with status {@code 500 (Internal Server Error)} if the laundryProgram couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/laundry-programs/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<LaundryProgram> partialUpdateLaundryProgram(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LaundryProgram laundryProgram
    ) throws URISyntaxException {
        log.debug("REST request to partial update LaundryProgram partially : {}, {}", id, laundryProgram);
        if (laundryProgram.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, laundryProgram.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!laundryProgramRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LaundryProgram> result = laundryProgramRepository
            .findById(laundryProgram.getId())
            .map(
                existingLaundryProgram -> {
                    if (laundryProgram.getName() != null) {
                        existingLaundryProgram.setName(laundryProgram.getName());
                    }
                    if (laundryProgram.getSubprogram() != null) {
                        existingLaundryProgram.setSubprogram(laundryProgram.getSubprogram());
                    }
                    if (laundryProgram.getSpin() != null) {
                        existingLaundryProgram.setSpin(laundryProgram.getSpin());
                    }
                    if (laundryProgram.getPreWash() != null) {
                        existingLaundryProgram.setPreWash(laundryProgram.getPreWash());
                    }
                    if (laundryProgram.getProtect() != null) {
                        existingLaundryProgram.setProtect(laundryProgram.getProtect());
                    }

                    return existingLaundryProgram;
                }
            )
            .map(laundryProgramRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, laundryProgram.getId().toString())
        );
    }

    /**
     * {@code GET  /laundry-programs} : get all the laundryPrograms.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of laundryPrograms in body.
     */
    @GetMapping("/laundry-programs")
    public List<LaundryProgram> getAllLaundryPrograms() {
        log.debug("REST request to get all LaundryPrograms");
        return laundryProgramRepository.findAll();
    }

    /**
     * {@code GET  /laundry-programs/:id} : get the "id" laundryProgram.
     *
     * @param id the id of the laundryProgram to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the laundryProgram, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/laundry-programs/{id}")
    public ResponseEntity<LaundryProgram> getLaundryProgram(@PathVariable Long id) {
        log.debug("REST request to get LaundryProgram : {}", id);
        Optional<LaundryProgram> laundryProgram = laundryProgramRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(laundryProgram);
    }

    /**
     * {@code DELETE  /laundry-programs/:id} : delete the "id" laundryProgram.
     *
     * @param id the id of the laundryProgram to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/laundry-programs/{id}")
    public ResponseEntity<Void> deleteLaundryProgram(@PathVariable Long id) {
        log.debug("REST request to delete LaundryProgram : {}", id);
        laundryProgramRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
