package de.farue.autocut.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.User;
import de.farue.autocut.repository.TenantRepository;

/**
 * Service Implementation for managing {@link Tenant}.
 */
@Service
@Transactional
public class TenantService {

    private final Logger log = LoggerFactory.getLogger(TenantService.class);

    private final TenantRepository tenantRepository;
    private final LeaseService leaseService;

    public TenantService(TenantRepository tenantRepository, LeaseService leaseService) {
        this.tenantRepository = tenantRepository;
        this.leaseService = leaseService;
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

    @Transactional(readOnly = true)
    public Optional<Tenant> findOneByUser(User user) {
        log.debug("Request to get Tenant by user: {}", user);
        return tenantRepository.findOneByUser(user);
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

    public Tenant createNewTenant(User user, Lease lease) {
        Tenant tenant = new Tenant();
        tenant.setUser(user);
        tenant.setVerified(false);
        tenant.setLease(lease);

        leaseService.save(lease);
        save(tenant);

        log.debug("Created new tenant: {}", tenant);
        return tenant;
    }
}
