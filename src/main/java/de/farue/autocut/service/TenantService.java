package de.farue.autocut.service;

import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.User;
import de.farue.autocut.domain.event.TenantCreatedEvent;
import de.farue.autocut.domain.event.TenantVerifiedEvent;
import de.farue.autocut.repository.TenantRepository;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
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
    private LeaseService leaseService;
    private final ApplicationEventPublisher publisher;
    private final UserService userService;

    public TenantService(
        TenantRepository tenantRepository,
        LeaseService leaseService,
        ApplicationEventPublisher publisher,
        UserService userService
    ) {
        this.tenantRepository = tenantRepository;
        this.leaseService = leaseService;
        this.publisher = publisher;
        this.userService = userService;
    }

    /**
     * Save a tenant.
     *
     * @param tenant the entity to save.
     * @return the persisted entity.
     */
    public Tenant save(Tenant tenant) {
        log.debug("Request to save Tenant : {}", tenant);

        boolean wasVerified = Optional.ofNullable(tenant.getId()).flatMap(this::findOne).map(Tenant::getVerified).orElse(false);

        boolean newEntity = tenant.getId() == null;
        tenant = tenantRepository.save(tenant);

        if (newEntity) {
            publisher.publishEvent(new TenantCreatedEvent(tenant));
        }
        if (!wasVerified && BooleanUtils.isTrue(tenant.getVerified())) {
            publisher.publishEvent(new TenantVerifiedEvent(tenant));
        }

        return tenant;
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
            .map(existingTenant -> {
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
            })
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

    public Tenant createNewTenant(String firstName, String lastName, User user, Lease lease) {
        Tenant tenant = new Tenant().firstName(firstName).lastName(lastName).user(user).verified(false).lease(lease);

        leaseService.save(lease);
        save(tenant);

        log.debug("Created new tenant: {}", tenant);
        return tenant;
    }

    public Optional<Tenant> getCurrentUserTenant() {
        return userService.getCurrentUser().flatMap(this::findOneByUser);
    }
}
