package de.farue.autocut.config;

import io.github.jhipster.config.JHipsterProperties;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Ehcache ehcache = jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class,
                ResourcePoolsBuilder.heap(ehcache.getMaxEntries()))
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
                .build());
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, de.farue.autocut.repository.UserRepository.USERS_BY_LOGIN_CACHE);
            createCache(cm, de.farue.autocut.repository.UserRepository.USERS_BY_EMAIL_CACHE);
            createCache(cm, de.farue.autocut.domain.User.class.getName());
            createCache(cm, de.farue.autocut.domain.Authority.class.getName());
            createCache(cm, de.farue.autocut.domain.User.class.getName() + ".authorities");
            createCache(cm, de.farue.autocut.domain.Tenant.class.getName());
            createCache(cm, de.farue.autocut.domain.Tenant.class.getName() + ".teamMemberships");
            createCache(cm, de.farue.autocut.domain.Tenant.class.getName() + ".messages");
            createCache(cm, de.farue.autocut.domain.Team.class.getName());
            createCache(cm, de.farue.autocut.domain.Team.class.getName() + ".members");
            createCache(cm, de.farue.autocut.domain.TeamMember.class.getName());
            createCache(cm, de.farue.autocut.domain.TeamMember.class.getName() + ".securityPolicies");
            createCache(cm, de.farue.autocut.domain.Lease.class.getName());
            createCache(cm, de.farue.autocut.domain.Lease.class.getName() + ".tenants");
            createCache(cm, de.farue.autocut.domain.Apartment.class.getName());
            createCache(cm, de.farue.autocut.domain.Apartment.class.getName() + ".leases");
            createCache(cm, de.farue.autocut.domain.Address.class.getName());
            createCache(cm, de.farue.autocut.domain.SecurityPolicy.class.getName());
            createCache(cm, de.farue.autocut.domain.InternetAccess.class.getName());
            createCache(cm, de.farue.autocut.domain.Port.class.getName());
            createCache(cm, de.farue.autocut.domain.NetworkSwitch.class.getName());
            createCache(cm, de.farue.autocut.domain.NetworkSwitch.class.getName() + ".ports");
            createCache(cm, de.farue.autocut.domain.PaymentAccount.class.getName());
            createCache(cm, de.farue.autocut.domain.PaymentAccount.class.getName() + ".transactions");
            createCache(cm, de.farue.autocut.domain.Transaction.class.getName());
            createCache(cm, de.farue.autocut.domain.TenantCommunication.class.getName());
            createCache(cm, de.farue.autocut.domain.Tenant.class.getName() + ".securityPolicies");
            createCache(cm, de.farue.autocut.domain.Tenant.class.getName() + ".activties");
            createCache(cm, de.farue.autocut.domain.PaymentAccount.class.getName() + ".paymentEntries");
            createCache(cm, de.farue.autocut.domain.PaymentEntry.class.getName());
            createCache(cm, de.farue.autocut.domain.Activity.class.getName());
            // jhipster-needle-ehcache-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cm.destroyCache(cacheName);
        }
        cm.createCache(cacheName, jcacheConfiguration);
    }

}
