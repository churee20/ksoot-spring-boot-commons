package com.ksoot.common.boot.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.r2dbc.spi.ConnectionFactory;

// Required only in case of r2dbms
//@Configuration
@ConditionalOnClass(value = { ConnectionFactory.class, Flyway.class })
@ConditionalOnProperty(prefix = "spring.flyway", name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(FlywayProperties.class)
@ConditionalOnMissingBean(Flyway.class)
public class FlywayConfig {

	private final FlywayProperties flywayProperties;

	public FlywayConfig(final FlywayProperties flywayProperties) {
		this.flywayProperties = flywayProperties;
	}

	@Bean(initMethod = "migrate")
	Flyway flyway() {
		return new Flyway(Flyway.configure().baselineOnMigrate(this.flywayProperties.isBaselineOnMigrate()).dataSource(
				this.flywayProperties.getUrl(), this.flywayProperties.getUser(), this.flywayProperties.getPassword()));
	}
}
