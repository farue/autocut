package de.farue.autocut.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.farue.autocut.domain.Apartment;
import de.farue.autocut.service.ApartmentService;
import de.farue.autocut.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;

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

    public ApartmentResource(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
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
        return ResponseEntity.created(new URI("/api/apartments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /apartments} : Updates an existing apartment.
     *
     * @param apartment the apartment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated apartment,
     * or with status {@code 400 (Bad Request)} if the apartment is not valid,
     * or with status {@code 500 (Internal Server Error)} if the apartment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/apartments")
    public ResponseEntity<Apartment> updateApartment(@Valid @RequestBody Apartment apartment) throws URISyntaxException {
        log.debug("REST request to update Apartment : {}", apartment);
        if (apartment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Apartment result = apartmentService.save(apartment);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, apartment.getId().toString()))
            .body(result);
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
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
