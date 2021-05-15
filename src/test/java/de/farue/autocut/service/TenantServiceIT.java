package de.farue.autocut.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.Apartment;
import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.enumeration.ApartmentTypes;
import de.farue.autocut.domain.event.TenantCreatedEvent;
import de.farue.autocut.domain.event.TenantVerifiedEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@SpringBootTest(classes = AutocutApp.class)
class TenantServiceIT {

    private static final String FIRST_NAME = "Alice";
    private static final String LAST_NAME = "Wonderland";
    private static final String OTHER_FIRST_NAME = "Bob";
    private static final String OTHER_LAST_NAME = "Miller";

    @Autowired
    private TenantService tenantService;

    @Autowired
    private LeaseService leaseService;

    @Autowired
    private ApartmentService apartmentService;

    @Autowired
    private EntityManager entityManager;

    @Nested
    @SpringBootTest(classes = AutocutApp.class)
    @RecordApplicationEvents
    class Events {

        @Autowired
        private ApplicationEvents applicationEvents;

        Lease lease;

        @BeforeEach
        void setUp() {
            Apartment apartment = apartmentService.save(new Apartment().nr("nr").type(ApartmentTypes.SINGLE).maxNumberOfLeases(1));
            this.lease =
                leaseService.save(
                    new Lease().start(LocalDate.of(2015, 10, 10)).end(LocalDate.of(2020, 9, 30)).nr("nr").apartment(apartment)
                );

            applicationEvents.clear();
        }

        @Test
        void testCreationShouldPublishEvent() {
            Tenant newTenant = tenantService.save(new Tenant().firstName(FIRST_NAME).lastName(LAST_NAME).verified(false).lease(lease));

            List<TenantCreatedEvent> events = applicationEvents.stream(TenantCreatedEvent.class).collect(Collectors.toList());
            assertThat(events).hasSize(1);
            TenantCreatedEvent event = events.get(0);
            assertThat(event.getTenant()).isEqualTo(newTenant);
        }

        @Test
        void testModificationShouldNotPublishCreationEvent() {
            Tenant newTenant = tenantService.save(new Tenant().firstName(FIRST_NAME).lastName(LAST_NAME).verified(false).lease(lease));
            // Disconnect from session so that the updates on updatedTenant are not directly saved in db
            entityManager.detach(newTenant);
            newTenant.setFirstName(OTHER_FIRST_NAME);
            newTenant.setLastName(OTHER_LAST_NAME);
            tenantService.save(newTenant);

            List<TenantCreatedEvent> events = applicationEvents.stream(TenantCreatedEvent.class).collect(Collectors.toList());
            assertThat(events).hasSize(1);
        }

        @Test
        void testVerifyingShouldPublishEvent() {
            Tenant newTenant = tenantService.save(new Tenant().firstName(FIRST_NAME).lastName(LAST_NAME).verified(false).lease(lease));
            // Disconnect from session so that the updates on updatedTenant are not directly saved in db
            entityManager.detach(newTenant);
            newTenant.setVerified(true);
            Tenant verifiedTenant = tenantService.save(newTenant);

            List<TenantVerifiedEvent> events = applicationEvents.stream(TenantVerifiedEvent.class).collect(Collectors.toList());
            assertThat(events).hasSize(1);
            TenantVerifiedEvent event = events.get(0);
            assertThat(event.getTenant()).isEqualTo(verifiedTenant);
        }
    }
}
