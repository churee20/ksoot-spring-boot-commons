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

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.ProblemHandling;

import com.ksoot.common.error.ApplicationException;
import com.ksoot.common.error.ApplicationProblem;

/**
 * @author Rajveer Singh
 */
@Configuration
@EnableConfigurationProperties(ProblemProperties.class)
@ConditionalOnProperty(prefix = "application.problem", name = "enabled", havingValue = "true")
@ConditionalOnClass(ProblemHandling.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ApplicationExceptionHandler extends AbstractAdviceTrait {

	private ProblemHelper problemHelper;

	public ApplicationExceptionHandler(final ProblemHelper problemHelper) {
		this.problemHelper = problemHelper;
	}

	@ExceptionHandler(ApplicationProblem.class)
	public ResponseEntity<Problem> handleApplicationProblem(final ApplicationProblem problem, 
			final NativeWebRequest request) {
		return this.problemHelper.createProblem(problem, request);
	}

	@ExceptionHandler(ApplicationException.class)
	public ResponseEntity<Problem> handleApplicationException(final ApplicationException exception,
			final NativeWebRequest request) {
		return this.problemHelper.createProblem(exception, request);
	}

	// Add more custom exception handlers below

}
