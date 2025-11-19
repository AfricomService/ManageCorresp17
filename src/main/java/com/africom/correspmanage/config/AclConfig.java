package com.africom.correspmanage.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Configuration
public class AclConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private CacheManager cacheManager; // HazelcastCacheManager

    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Bean
    public PermissionGrantingStrategy permissionGrantingStrategy() {
        return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
    }

    /**
     * Create the AclCache using the generic Spring Cache abstraction.
     * Works with Hazelcast.
     */
    @Bean
    public AclCache aclCache() {
        org.springframework.cache.Cache springCache = cacheManager.getCache("aclCache");
        if (springCache == null) {
            return new SpringCacheBasedAclCache(
                new org.springframework.cache.concurrent.ConcurrentMapCache("aclCache"),
                permissionGrantingStrategy(),
                aclAuthorizationStrategy()
            );
        }
        return new SpringCacheBasedAclCache(springCache, permissionGrantingStrategy(), aclAuthorizationStrategy());
    }

    @Bean
    public LookupStrategy lookupStrategy(AclCache aclCache) {
        return new BasicLookupStrategy(dataSource, aclCache, aclAuthorizationStrategy(), new ConsoleAuditLogger());
    }

    @Bean
    public JdbcMutableAclService aclService(LookupStrategy lookupStrategy, AclCache aclCache) {
        JdbcMutableAclService aclService = new JdbcMutableAclService(dataSource, lookupStrategy, aclCache);

        aclService.setClassIdentityQuery("SELECT currval(pg_get_serial_sequence('acl_class', 'id'))");
        aclService.setSidIdentityQuery("SELECT currval(pg_get_serial_sequence('acl_sid', 'id'))");

        return aclService;
    }
}
