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

import de.farue.autocut.domain.Address;
import de.farue.autocut.repository.AddressRepository;
import de.farue.autocut.service.AddressService;
import de.farue.autocut.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link de.farue.autocut.domain.Address}.
 */
@RestController
@RequestMapping("/api")
public class AddressResource {

    private final Logger log = LoggerFactory.getLogger(AddressResource.class);

    private static final String ENTITY_NAME = "address";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AddressService addressService;

    private final AddressRepository addressRepository;

    public AddressResource(AddressService addressService, AddressRepository addressRepository) {
        this.addressService = addressService;
        this.addressRepository = addressRepository;
    }

    /**
     * {@code POST  /addresses} : Create a new address.
     *
     * @param address the address to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new address, or with status {@code 400 (Bad Request)} if the address has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/addresses")
    public ResponseEntity<Address> createAddress(@Valid @RequestBody Address address) throws URISyntaxException {
        log.debug("REST request to save Address : {}", address);
        if (address.getId() != null) {
            throw new BadRequestAlertException("A new address cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Address result = addressService.save(address);
        return ResponseEntity
            .created(new URI("/api/addresses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /addresses/:id} : Updates an existing address.
     *
     * @param id the id of the address to save.
     * @param address the address to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated address,
     * or with status {@code 400 (Bad Request)} if the address is not valid,
     * or with status {@code 500 (Internal Server Error)} if the address couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/addresses/{id}")
    public ResponseEntity<Address> updateAddress(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Address address
    ) throws URISyntaxException {
        log.debug("REST request to update Address : {}, {}", id, address);
        if (address.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, address.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!addressRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Address result = addressService.save(address);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, address.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /addresses/:id} : Partial updates given fields of an existing address, field will ignore if it is null
     *
     * @param id the id of the address to save.
     * @param address the address to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated address,
     * or with status {@code 400 (Bad Request)} if the address is not valid,
     * or with status {@code 404 (Not Found)} if the address is not found,
     * or with status {@code 500 (Internal Server Error)} if the address couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/addresses/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Address> partialUpdateAddress(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Address address
    ) throws URISyntaxException {
        log.debug("REST request to partial update Address partially : {}, {}", id, address);
        if (address.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, address.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!addressRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Address> result = addressService.partialUpdate(address);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, address.getId().toString())
        );
    }

    /**
     * {@code GET  /addresses} : get all the addresses.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of addresses in body.
     */
    @GetMapping("/addresses")
    public List<Address> getAllAddresses() {
        log.debug("REST request to get all Addresses");
        return addressService.findAll();
    }

    /**
     * {@code GET  /addresses/:id} : get the "id" address.
     *
     * @param id the id of the address to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the address, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/addresses/{id}")
    public ResponseEntity<Address> getAddress(@PathVariable Long id) {
        log.debug("REST request to get Address : {}", id);
        Optional<Address> address = addressService.findOne(id);
        return ResponseUtil.wrapOrNotFound(address);
    }

    /**
     * {@code DELETE  /addresses/:id} : delete the "id" address.
     *
     * @param id the id of the address to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        log.debug("REST request to delete Address : {}", id);
        addressService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
