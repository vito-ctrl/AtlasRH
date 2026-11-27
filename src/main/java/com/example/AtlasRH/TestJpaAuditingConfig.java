package com.example.AtlasRH;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;

@AutoConfiguration
public class TestJpaAuditingConfig {

    @Bean
    @ConditionalOnMissingBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext() {
        return new JpaMetamodelMappingContext(
                java.util.Collections.emptySet()
        );
    }
}