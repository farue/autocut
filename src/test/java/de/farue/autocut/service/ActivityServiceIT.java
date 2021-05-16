package de.farue.autocut.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.Activity;
import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.enumeration.SemesterTerms;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@IntegrationTest
public class ActivityServiceIT {

    private static final String ANY_NR = "nr";
    private static final LocalDate ANY_START_DATE = LocalDate.of(2010, 1, 1);
    private static final LocalDate ANY_END_DATE = LocalDate.of(2020, 1, 1);
    private static final LocalDate DATE_DURING_ACTIVITY = LocalDate.of(2019, 3, 10);
    private static final LocalDate DATE_BEFORE_ACTIVITY = LocalDate.of(2015, 12, 12);
    private static final LocalDate DATE_AFTER_ACTIVITY = LocalDate.of(2019, 4, 1);
    private static final String TENANT1_FIRST_NAME = "bob";
    private static final String TENANT1_LAST_NAME = "miller";
    private static final String TENANT2_FIRST_NAME = "celine";
    private static final String TENANT2_LAST_NAME = "doe";

    @Autowired
    private LeaseService leaseService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private ActivityService activityService;

    @Nested
    class FindActivitOn {

        private Lease lease;
        private Tenant tenant1;
        private Tenant tenant2;

        @BeforeEach
        void setUp() {
            Tenant tenant1 = new Tenant().firstName(TENANT1_FIRST_NAME).lastName(TENANT1_LAST_NAME);

            Tenant tenant2 = new Tenant().firstName(TENANT2_FIRST_NAME).lastName(TENANT2_LAST_NAME);

            Lease lease = new Lease().nr(ANY_NR).start(ANY_START_DATE).end(ANY_END_DATE).addTenants(tenant1).addTenants(tenant2);

            this.lease = leaseService.save(lease);
            this.tenant1 = tenantService.save(tenant1);
            this.tenant2 = tenantService.save(tenant2);
        }

        @Nested
        class ShouldNotFindAnyActivity {

            @Test
            void noActivity() {
                assertThat(activityService.findActivityOn(lease, DATE_DURING_ACTIVITY)).isEmpty();
            }

            @Test
            void activityInPast() {
                Activity activity = new Activity().tenant(tenant1).year(2018).term(SemesterTerms.WINTER_TERM);
                activityService.save(activity);

                assertThat(activityService.findActivityOn(lease, DATE_AFTER_ACTIVITY)).isEmpty();
            }

            @Test
            void activityInFuture() {
                Activity activity = new Activity().tenant(tenant1).year(2018).term(SemesterTerms.WINTER_TERM);
                activityService.save(activity);

                assertThat(activityService.findActivityOn(lease, DATE_BEFORE_ACTIVITY)).isEmpty();
            }

            @Test
            void activityOfUnrelatedTenants() {
                Tenant unrelatedTenant = new Tenant().firstName(TENANT1_FIRST_NAME).lastName(TENANT1_LAST_NAME);
                Lease unrelatedLease = new Lease().nr("other nr").start(ANY_START_DATE).end(ANY_END_DATE).addTenants(unrelatedTenant);

                leaseService.save(unrelatedLease);
                tenantService.save(unrelatedTenant);

                Activity activity = new Activity().tenant(unrelatedTenant).year(2018).term(SemesterTerms.WINTER_TERM);
                activityService.save(activity);

                assertThat(activityService.findActivityOn(lease, DATE_BEFORE_ACTIVITY)).isEmpty();
            }
        }

        @Nested
        class ShouldFindActivity {

            @Test
            void activityDuringSemesterTerm() {
                Activity activity = new Activity().tenant(tenant1).year(2018).term(SemesterTerms.WINTER_TERM);
                activityService.save(activity);

                assertThat(activityService.findActivityOn(lease, DATE_DURING_ACTIVITY)).contains(activity);
            }

            @Test
            void activityOfMultipleTenantsInLease() {
                Activity activity1Tenant1 = new Activity().tenant(tenant1).year(2018).term(SemesterTerms.WINTER_TERM);
                Activity activity2Tenant1 = new Activity().tenant(tenant1).year(2018).term(SemesterTerms.WINTER_TERM);
                Activity activity1Tenant2 = new Activity().tenant(tenant2).year(2018).term(SemesterTerms.WINTER_TERM);
                activityService.save(activity1Tenant1);
                activityService.save(activity2Tenant1);
                activityService.save(activity1Tenant2);

                assertThat(activityService.findActivityOn(lease, DATE_DURING_ACTIVITY))
                    .containsExactlyInAnyOrder(activity1Tenant1, activity1Tenant2, activity2Tenant1);
            }
        }
    }
}
