package de.farue.autocut.service;

import de.farue.autocut.domain.Tenant;
import de.farue.autocut.repository.TenantRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Tenant}.
 */
@Service
@Transactional
public class TenantService {

    private final Logger log = LoggerFactory.getLogger(TenantService.class);

    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    /**
     * Save a tenant.
     *
     * @param tenant the entity to save.
     * @return the persisted entity.
     */
    public Tenant save(Tenant tenant) {
        log.debug("Request to save Tenant : {}", tenant);
        return tenantRepository.save(tenant);
    }

    /**
     * Partially update a tenant.
     *
     * @param tenant the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Tenant> partialUpdate(Tenant tenant) {
        log.debug("Request to partially update Tenant : {}", tenant);

        return tenantRepository
            .findById(tenant.getId())
            .map(
                existingTenant -> {
                    if (tenant.getFirstName() != null) {
                        existingTenant.setFirstName(tenant.getFirstName());
                    }
                    if (tenant.getLastName() != null) {
                        existingTenant.setLastName(tenant.getLastName());
                    }
                    if (tenant.getPictureId() != null) {
                        existingTenant.setPictureId(tenant.getPictureId());
                    }
                    if (tenant.getPictureIdContentType() != null) {
                        existingTenant.setPictureIdContentType(tenant.getPictureIdContentType());
                    }
                    if (tenant.getVerified() != null) {
                        existingTenant.setVerified(tenant.getVerified());
                    }

                    return existingTenant;
                }
            )
            .map(tenantRepository::save);
    }

    /**
     * Get all the tenants.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Tenant> findAll() {
        log.debug("Request to get all Tenants");
        return tenantRepository.findAll();
    }

    /**
     * Get one tenant by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Tenant> findOne(Long id) {
        log.debug("Request to get Tenant : {}", id);
        return tenantRepository.findById(id);
    }

    /**
     * Delete the tenant by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Tenant : {}", id);
        tenantRepository.deleteById(id);
    }
}
