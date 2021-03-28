package de.farue.autocut.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.User;
import de.farue.autocut.repository.TenantRepository;
import de.farue.autocut.repository.UserRepository;
import java.util.Locale;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integrations tests for {@link DomainUserDetailsService}.
 */
@Transactional
@IntegrationTest
class DomainUserDetailsServiceIT {

    private static final String USER_ONE_LOGIN = "test-user-one";
    private static final String USER_ONE_EMAIL = "test-user-one@localhost";
    private static final String USER_TWO_LOGIN = "test-user-two";
    private static final String USER_TWO_EMAIL = "test-user-two@localhost";
    private static final String USER_THREE_LOGIN = "test-user-three";
    private static final String USER_THREE_EMAIL = "test-user-three@localhost";
    private static final String USER_WITH_TENANT_NOT_VERIFIED_LOGIN = "test-user-four";
    private static final String USER_WITH_TENANT_NOT_VERIFIED_EMAIL = "test-user-four@localhost";
    private static final String USER_WITH_TENANT_VERIFIED_LOGIN = "test-user-five";
    private static final String USER_WITH_TENANT_VERIFIED_EMAIL = "test-user-five@localhost";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private UserDetailsService domainUserDetailsService;

    @BeforeEach
    public void init() {
        User userOne = new User();
        userOne.setLogin(USER_ONE_LOGIN);
        userOne.setPassword(RandomStringUtils.random(60));
        userOne.setActivated(true);
        userOne.setEmail(USER_ONE_EMAIL);
        userOne.setFirstName("userOne");
        userOne.setLastName("doe");
        userOne.setLangKey("en");
        userRepository.save(userOne);

        User userTwo = new User();
        userTwo.setLogin(USER_TWO_LOGIN);
        userTwo.setPassword(RandomStringUtils.random(60));
        userTwo.setActivated(true);
        userTwo.setEmail(USER_TWO_EMAIL);
        userTwo.setFirstName("userTwo");
        userTwo.setLastName("doe");
        userTwo.setLangKey("en");
        userRepository.save(userTwo);

        User userThree = new User();
        userThree.setLogin(USER_THREE_LOGIN);
        userThree.setPassword(RandomStringUtils.random(60));
        userThree.setActivated(false);
        userThree.setEmail(USER_THREE_EMAIL);
        userThree.setFirstName("userThree");
        userThree.setLastName("doe");
        userThree.setLangKey("en");
        userRepository.save(userThree);

        User userFour = new User();
        userFour.setLogin(USER_WITH_TENANT_NOT_VERIFIED_LOGIN);
        userFour.setPassword(RandomStringUtils.random(60));
        userFour.setActivated(true);
        userFour.setEmail(USER_WITH_TENANT_NOT_VERIFIED_EMAIL);
        userFour.setFirstName("userFour");
        userFour.setLastName("doe");
        userFour.setLangKey("en");
        userRepository.save(userFour);

        Tenant tenantNotVerified = new Tenant()
            .firstName("tenantOne")
            .lastName("notVerified")
            .user(userFour)
            .verified(false);
        tenantRepository.save(tenantNotVerified);

        User userFive = new User();
        userFive.setLogin(USER_WITH_TENANT_VERIFIED_LOGIN);
        userFive.setPassword(RandomStringUtils.random(60));
        userFive.setActivated(true);
        userFive.setEmail(USER_WITH_TENANT_VERIFIED_EMAIL);
        userFive.setFirstName("userFour");
        userFive.setLastName("doe");
        userFive.setLangKey("en");
        userRepository.save(userFive);

        Tenant tenantVerified = new Tenant()
            .firstName("tenantTwo")
            .lastName("verified")
            .user(userFive)
            .verified(true);
        tenantRepository.save(tenantVerified);
    }

    @Test
    void assertThatUserCanBeFoundByLogin() {
        UserDetails userDetails = domainUserDetailsService.loadUserByUsername(USER_ONE_LOGIN);
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(USER_ONE_LOGIN);
    }

    @Test
    void assertThatUserCanBeFoundByLoginIgnoreCase() {
        UserDetails userDetails = domainUserDetailsService.loadUserByUsername(USER_ONE_LOGIN.toUpperCase(Locale.ENGLISH));
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(USER_ONE_LOGIN);
    }

    @Test
    void assertThatUserCanBeFoundByEmail() {
        UserDetails userDetails = domainUserDetailsService.loadUserByUsername(USER_TWO_EMAIL);
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(USER_TWO_LOGIN);
    }

    @Test
    void assertThatUserCanBeFoundByEmailIgnoreCase() {
        UserDetails userDetails = domainUserDetailsService.loadUserByUsername(USER_TWO_EMAIL.toUpperCase(Locale.ENGLISH));
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(USER_TWO_LOGIN);
    }

    @Test
    void assertThatEmailIsPrioritizedOverLogin() {
        UserDetails userDetails = domainUserDetailsService.loadUserByUsername(USER_ONE_EMAIL);
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(USER_ONE_LOGIN);
    }

    @Test
    void assertThatUserNotActivatedExceptionIsThrownForNotActivatedUsers() {
        assertThatExceptionOfType(UserNotActivatedException.class)
            .isThrownBy(() -> domainUserDetailsService.loadUserByUsername(USER_THREE_LOGIN));
    }

    @Test
    void assertThatUserNotVerifiedExceptionIsThrownForNotUsersWithNotVerifiedTenant() {
        assertThatExceptionOfType(UserNotVerifiedException.class).isThrownBy(
            () -> domainUserDetailsService.loadUserByUsername(USER_WITH_TENANT_NOT_VERIFIED_LOGIN));
    }

    @Test
    void assertThatUserCanBeFoundWhenTenantgetVerified() {
        UserDetails userDetails = domainUserDetailsService.loadUserByUsername(USER_WITH_TENANT_VERIFIED_LOGIN);
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(USER_WITH_TENANT_VERIFIED_LOGIN);
    }
}
