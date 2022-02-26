package com.ksoot.common.boot.config.error;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * Condition to trigger introduction of advice for Security exception handling.
 */
public class SecurityExceptionAdviceConfigurationCondition extends AllNestedConditions {

	SecurityExceptionAdviceConfigurationCondition() {
		super(ConfigurationPhase.PARSE_CONFIGURATION);
	}

	@ConditionalOnProperty(prefix = "application.security", name = "enabled", havingValue = "true")
	static class SecurityEnabledProperty {

	}

	@ConditionalOnProperty(prefix = "application.problem", name = "enabled", havingValue = "true")
	static class ProblemEnabledProperty {

	}
}
