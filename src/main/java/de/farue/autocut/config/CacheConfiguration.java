package de.farue.autocut.config;

import java.time.Duration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.jhipster.config.JHipsterProperties;
import tech.jhipster.config.cache.PrefixedKeyGenerator;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private GitProperties gitProperties;
    private BuildProperties buildProperties;
    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Ehcache ehcache = jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration =
            Eh107Configuration.fromEhcacheCacheConfiguration(
                CacheConfigurationBuilder
                    .newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(ehcache.getMaxEntries()))
                    .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
                    .build()
            );
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
            createCache(cm, de.farue.autocut.domain.Tenant.class.getName() + ".securityPolicies");
            createCache(cm, de.farue.autocut.domain.Team.class.getName());
            createCache(cm, de.farue.autocut.domain.Team.class.getName() + ".teamMemberships");
            createCache(cm, de.farue.autocut.domain.Lease.class.getName());
            createCache(cm, de.farue.autocut.domain.Lease.class.getName() + ".tenants");
            createCache(cm, de.farue.autocut.domain.Lease.class.getName() + ".transactionBooks");
            createCache(cm, de.farue.autocut.domain.Apartment.class.getName());
            createCache(cm, de.farue.autocut.domain.Apartment.class.getName() + ".leases");
            createCache(cm, de.farue.autocut.domain.Address.class.getName());
            createCache(cm, de.farue.autocut.domain.SecurityPolicy.class.getName());
            createCache(cm, de.farue.autocut.domain.InternetAccess.class.getName());
            createCache(cm, de.farue.autocut.domain.InternalTransaction.class.getName());
            createCache(cm, de.farue.autocut.domain.InternalTransaction.class.getName() + ".lefts");
            createCache(cm, de.farue.autocut.domain.InternalTransaction.class.getName() + ".rights");
            createCache(cm, de.farue.autocut.domain.TenantCommunication.class.getName());
            createCache(cm, de.farue.autocut.domain.Activity.class.getName());
            createCache(cm, de.farue.autocut.domain.Communication.class.getName());
            createCache(cm, de.farue.autocut.domain.LaundryMachine.class.getName());
            createCache(cm, de.farue.autocut.domain.LaundryMachine.class.getName() + ".programs");
            createCache(cm, de.farue.autocut.domain.LaundryMachineProgram.class.getName());
            createCache(cm, de.farue.autocut.domain.WashHistory.class.getName());
            createCache(cm, de.farue.autocut.domain.GlobalSetting.class.getName());
            createCache(cm, de.farue.autocut.domain.NetworkSwitch.class.getName());
            createCache(cm, de.farue.autocut.domain.TransactionBook.class.getName());
            createCache(cm, de.farue.autocut.domain.TransactionBook.class.getName() + ".iTransactions");
            createCache(cm, de.farue.autocut.domain.TransactionBook.class.getName() + ".bTransactions");
            createCache(cm, de.farue.autocut.domain.TransactionBook.class.getName() + ".leases");
            createCache(cm, de.farue.autocut.domain.TeamMembership.class.getName());
            createCache(cm, de.farue.autocut.domain.TeamMembership.class.getName() + ".securityPolicies");
            createCache(cm, de.farue.autocut.domain.BankAccount.class.getName());
            createCache(cm, de.farue.autocut.domain.BankTransaction.class.getName());
            createCache(cm, de.farue.autocut.domain.BankTransaction.class.getName() + ".lefts");
            createCache(cm, de.farue.autocut.domain.BankTransaction.class.getName() + ".rights");
            createCache(cm, de.farue.autocut.domain.NetworkSwitchStatus.class.getName());
            createCache(cm, de.farue.autocut.domain.LaundryProgram.class.getName());
            createCache(cm, de.farue.autocut.domain.BroadcastMessage.class.getName());
            createCache(cm, de.farue.autocut.domain.BroadcastMessage.class.getName() + ".messageTexts");
            createCache(cm, de.farue.autocut.domain.BroadcastMessageText.class.getName());
            // jhipster-needle-ehcache-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }

    @Autowired(required = false)
    public void setGitProperties(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
    }

    @Autowired(required = false)
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new PrefixedKeyGenerator(this.gitProperties, this.buildProperties);
    }
}
