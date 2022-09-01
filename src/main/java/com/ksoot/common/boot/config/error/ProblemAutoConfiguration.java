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

package com.ksoot.common.boot.config.error;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.zalando.problem.spring.web.advice.ProblemHandling;

/**
 * @author Rajveer Singh
 */
@Configuration
@EnableConfigurationProperties(ProblemProperties.class)
@ConditionalOnProperty(prefix = "application.problem", name = "enabled", havingValue = "true")
@ConditionalOnClass(ProblemHandling.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Import({ ApplicationExceptionHandler.class, SecurityExceptionHandler.class, WebExceptionHandler.class })
public class ProblemAutoConfiguration {

	@ConditionalOnMissingBean(name = "problemHelper")
	@Bean
	ProblemHelper problemHelper(final ErrorBuilder errorBuilder, final ProblemProperties problemProperties) {
		return new ProblemHelper(errorBuilder, problemProperties);
	}
	
	@ConditionalOnMissingBean(name = "errorBuilder")
	@Bean
	ErrorBuilder errorBuilder(@Value("${server.servlet.context-path:}") String contextPath) {
		return new DefaultErrorBuilder(contextPath);
	}
}
