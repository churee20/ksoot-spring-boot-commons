/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ksoot.common.boot.config;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.zalando.jackson.datatype.money.MoneyModule;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * @author Rajveer Singh
 */
@Configuration
@ConditionalOnMissingBean(JacksonAutoConfiguration.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class JacksonAutoConfiguration {

	@Configuration
	@ConditionalOnClass(JavaTimeModule.class)
	public static class JavaTimeModuleConfiguration {

		@Bean
		@ConditionalOnMissingBean
		JavaTimeModule javaTimeModule() {
			return new JavaTimeModule();
		}

	}

	@Configuration
	@ConditionalOnMissingClass("org.zalando.problem.spring.web.autoconfigure.ProblemJacksonAutoConfiguration")
	@ConditionalOnClass(ProblemModule.class)
	public static class ProblemModuleConfiguration {

		@Bean
		@ConditionalOnMissingBean
		ProblemModule problemModule() {
			return new ProblemModule();
		}

	}

	@Configuration
	@ConditionalOnMissingClass("org.zalando.problem.spring.web.autoconfigure.ProblemJacksonAutoConfiguration")
	@ConditionalOnClass(ConstraintViolationProblemModule.class)
	public static class ConstraintViolationProblemModuleConfiguration {

		@Bean
		@ConditionalOnMissingBean
		ConstraintViolationProblemModule constraintViolationProblemModule() {
			return new ConstraintViolationProblemModule();
		}

	}

	@Configuration
	@ConditionalOnClass(MoneyModule.class)
	public static class MoneyModuleConfiguration {

		@Bean
		@ConditionalOnMissingBean
		MoneyModule moneyModule() {
			return new MoneyModule();
		}
	}

}
