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

package com.ksoot.common.boot.config.error.reactive;

import static com.ksoot.common.boot.BootConstant.BeanName.PROBLEM_AUTO_CONFIGURATION_BEAN_NAME;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;
import org.zalando.problem.spring.webflux.advice.ProblemHandling;

import com.ksoot.common.boot.config.CommonComponentsAutoConfiguration;
import com.ksoot.common.boot.config.error.ProblemProperties;
import com.ksoot.common.boot.config.error.db.ConstraintNameResolver;

/**
 * @author Rajveer Singh
 */
@Configuration(value = PROBLEM_AUTO_CONFIGURATION_BEAN_NAME)
@EnableConfigurationProperties(ProblemProperties.class)
@ConditionalOnProperty(prefix = "application.problem", name = "enabled", havingValue = "true")
@ConditionalOnClass(ProblemHandling.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnMissingBean(name = PROBLEM_AUTO_CONFIGURATION_BEAN_NAME)
@AutoConfigureAfter(value = CommonComponentsAutoConfiguration.class)
@Import({ ReactiveApplicationExceptionHandler.class, ReactiveSecurityExceptionHandler.class, /*
																								 * CircuitBreakerExceptionHandler
																								 * .class,
																								 */
		ReactiveWebExceptionHandler.class })
public class ReactiveProblemAutoConfiguration {

	@ConditionalOnMissingBean(name = "problemHelper")
	@Bean
	public ReactiveProblemHelper problemHelper(@Value("${web-application-type}") 
			final WebApplicationType webApplicationType,
			final Environment env, final ProblemProperties problemProperties,
			@Nullable final ConstraintNameResolver constraintNameResolver) {
		return new ReactiveProblemHelper(env, problemProperties, constraintNameResolver);
	}

}
