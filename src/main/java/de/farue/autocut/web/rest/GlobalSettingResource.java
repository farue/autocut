package de.farue.autocut.web.rest;

import de.farue.autocut.domain.GlobalSetting;
import de.farue.autocut.repository.GlobalSettingRepository;
import de.farue.autocut.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link de.farue.autocut.domain.GlobalSetting}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class GlobalSettingResource {

    private final Logger log = LoggerFactory.getLogger(GlobalSettingResource.class);

    private static final String ENTITY_NAME = "globalSetting";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GlobalSettingRepository globalSettingRepository;

    public GlobalSettingResource(GlobalSettingRepository globalSettingRepository) {
        this.globalSettingRepository = globalSettingRepository;
    }

    /**
     * {@code POST  /global-settings} : Create a new globalSetting.
     *
     * @param globalSetting the globalSetting to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new globalSetting, or with status {@code 400 (Bad Request)} if the globalSetting has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/global-settings")
    public ResponseEntity<GlobalSetting> createGlobalSetting(@RequestBody GlobalSetting globalSetting) throws URISyntaxException {
        log.debug("REST request to save GlobalSetting : {}", globalSetting);
        if (globalSetting.getId() != null) {
            throw new BadRequestAlertException("A new globalSetting cannot already have an ID", ENTITY_NAME, "idexists");
        }
        GlobalSetting result = globalSettingRepository.save(globalSetting);
        return ResponseEntity
            .created(new URI("/api/global-settings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /global-settings/:id} : Updates an existing globalSetting.
     *
     * @param id the id of the globalSetting to save.
     * @param globalSetting the globalSetting to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated globalSetting,
     * or with status {@code 400 (Bad Request)} if the globalSetting is not valid,
     * or with status {@code 500 (Internal Server Error)} if the globalSetting couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/global-settings/{id}")
    public ResponseEntity<GlobalSetting> updateGlobalSetting(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody GlobalSetting globalSetting
    ) throws URISyntaxException {
        log.debug("REST request to update GlobalSetting : {}, {}", id, globalSetting);
        if (globalSetting.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, globalSetting.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!globalSettingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        GlobalSetting result = globalSettingRepository.save(globalSetting);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, globalSetting.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /global-settings/:id} : Partial updates given fields of an existing globalSetting, field will ignore if it is null
     *
     * @param id the id of the globalSetting to save.
     * @param globalSetting the globalSetting to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated globalSetting,
     * or with status {@code 400 (Bad Request)} if the globalSetting is not valid,
     * or with status {@code 404 (Not Found)} if the globalSetting is not found,
     * or with status {@code 500 (Internal Server Error)} if the globalSetting couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/global-settings/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<GlobalSetting> partialUpdateGlobalSetting(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody GlobalSetting globalSetting
    ) throws URISyntaxException {
        log.debug("REST request to partial update GlobalSetting partially : {}, {}", id, globalSetting);
        if (globalSetting.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, globalSetting.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!globalSettingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<GlobalSetting> result = globalSettingRepository
            .findById(globalSetting.getId())
            .map(
                existingGlobalSetting -> {
                    if (globalSetting.getKey() != null) {
                        existingGlobalSetting.setKey(globalSetting.getKey());
                    }
                    if (globalSetting.getValue() != null) {
                        existingGlobalSetting.setValue(globalSetting.getValue());
                    }
                    if (globalSetting.getValueType() != null) {
                        existingGlobalSetting.setValueType(globalSetting.getValueType());
                    }

                    return existingGlobalSetting;
                }
            )
            .map(globalSettingRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, globalSetting.getId().toString())
        );
    }

    /**
     * {@code GET  /global-settings} : get all the globalSettings.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of globalSettings in body.
     */
    @GetMapping("/global-settings")
    public List<GlobalSetting> getAllGlobalSettings() {
        log.debug("REST request to get all GlobalSettings");
        return globalSettingRepository.findAll();
    }

    /**
     * {@code GET  /global-settings/:id} : get the "id" globalSetting.
     *
     * @param id the id of the globalSetting to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the globalSetting, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/global-settings/{id}")
    public ResponseEntity<GlobalSetting> getGlobalSetting(@PathVariable Long id) {
        log.debug("REST request to get GlobalSetting : {}", id);
        Optional<GlobalSetting> globalSetting = globalSettingRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(globalSetting);
    }

    /**
     * {@code DELETE  /global-settings/:id} : delete the "id" globalSetting.
     *
     * @param id the id of the globalSetting to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/global-settings/{id}")
    public ResponseEntity<Void> deleteGlobalSetting(@PathVariable Long id) {
        log.debug("REST request to delete GlobalSetting : {}", id);
        globalSettingRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
