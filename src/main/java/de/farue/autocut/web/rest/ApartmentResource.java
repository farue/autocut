package de.farue.autocut.web.rest;

import de.farue.autocut.domain.Apartment;
import de.farue.autocut.repository.ApartmentRepository;
import de.farue.autocut.service.ApartmentService;
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
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link de.farue.autocut.domain.Apartment}.
 */
@RestController
@RequestMapping("/api")
public class ApartmentResource {

    private final Logger log = LoggerFactory.getLogger(ApartmentResource.class);

    private static final String ENTITY_NAME = "apartment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ApartmentService apartmentService;

    private final ApartmentRepository apartmentRepository;

    public ApartmentResource(ApartmentService apartmentService, ApartmentRepository apartmentRepository) {
        this.apartmentService = apartmentService;
        this.apartmentRepository = apartmentRepository;
    }

    /**
     * {@code POST  /apartments} : Create a new apartment.
     *
     * @param apartment the apartment to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new apartment, or with status {@code 400 (Bad Request)} if the apartment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/apartments")
    public ResponseEntity<Apartment> createApartment(@Valid @RequestBody Apartment apartment) throws URISyntaxException {
        log.debug("REST request to save Apartment : {}", apartment);
        if (apartment.getId() != null) {
            throw new BadRequestAlertException("A new apartment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Apartment result = apartmentService.save(apartment);
        return ResponseEntity
            .created(new URI("/api/apartments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /apartments/:id} : Updates an existing apartment.
     *
     * @param id the id of the apartment to save.
     * @param apartment the apartment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated apartment,
     * or with status {@code 400 (Bad Request)} if the apartment is not valid,
     * or with status {@code 500 (Internal Server Error)} if the apartment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/apartments/{id}")
    public ResponseEntity<Apartment> updateApartment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Apartment apartment
    ) throws URISyntaxException {
        log.debug("REST request to update Apartment : {}, {}", id, apartment);
        if (apartment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, apartment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!apartmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Apartment result = apartmentService.save(apartment);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, apartment.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /apartments/:id} : Partial updates given fields of an existing apartment, field will ignore if it is null
     *
     * @param id the id of the apartment to save.
     * @param apartment the apartment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated apartment,
     * or with status {@code 400 (Bad Request)} if the apartment is not valid,
     * or with status {@code 404 (Not Found)} if the apartment is not found,
     * or with status {@code 500 (Internal Server Error)} if the apartment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/apartments/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Apartment> partialUpdateApartment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Apartment apartment
    ) throws URISyntaxException {
        log.debug("REST request to partial update Apartment partially : {}, {}", id, apartment);
        if (apartment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, apartment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!apartmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Apartment> result = apartmentService.partialUpdate(apartment);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, apartment.getId().toString())
        );
    }

    /**
     * {@code GET  /apartments} : get all the apartments.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of apartments in body.
     */
    @GetMapping("/apartments")
    public List<Apartment> getAllApartments() {
        log.debug("REST request to get all Apartments");
        return apartmentService.findAll();
    }

    /**
     * {@code GET  /apartments/:id} : get the "id" apartment.
     *
     * @param id the id of the apartment to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the apartment, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/apartments/{id}")
    public ResponseEntity<Apartment> getApartment(@PathVariable Long id) {
        log.debug("REST request to get Apartment : {}", id);
        Optional<Apartment> apartment = apartmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(apartment);
    }

    /**
     * {@code DELETE  /apartments/:id} : delete the "id" apartment.
     *
     * @param id the id of the apartment to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/apartments/{id}")
    public ResponseEntity<Void> deleteApartment(@PathVariable Long id) {
        log.debug("REST request to delete Apartment : {}", id);
        apartmentService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
