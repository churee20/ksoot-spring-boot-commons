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

import static com.ksoot.common.boot.BootConstant.BeanName.APPLICATION_EXCEPTION_HANDLER_BEAN_NAME;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.webflux.advice.AdviceTrait;
import org.zalando.problem.spring.webflux.advice.ProblemHandling;

import com.ksoot.common.boot.config.error.ProblemProperties;
import com.ksoot.common.error.ApplicationException;
import com.ksoot.common.error.ApplicationProblem;

import reactor.core.publisher.Mono;

/**
 * @author Rajveer Singh
 */
@Configuration(value = APPLICATION_EXCEPTION_HANDLER_BEAN_NAME)
@EnableConfigurationProperties(ProblemProperties.class)
@ConditionalOnProperty(prefix = "application.problem", name = "enabled", havingValue = "true")
@ConditionalOnClass(ProblemHandling.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnMissingBean(name = APPLICATION_EXCEPTION_HANDLER_BEAN_NAME)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ReactiveApplicationExceptionHandler extends AbstractAdviceTrait implements AdviceTrait {

	private ReactiveProblemHelper problemHelper;

	public ReactiveApplicationExceptionHandler(final ReactiveProblemHelper problemHelper) {
		this.problemHelper = problemHelper;
	}

	@ExceptionHandler(ApplicationProblem.class)
	public Mono<ResponseEntity<Problem>> handleApplicationProblem(final ApplicationProblem problem,
			final ServerWebExchange request) {
		return this.problemHelper.createProblem(problem, request);
	}

	@ExceptionHandler(ApplicationException.class)
	public Mono<ResponseEntity<Problem>> handleApplicationException(final ApplicationException exception,
			final ServerWebExchange request) {
		return this.problemHelper.createProblem(exception, request);
	}

	// Add more custom exception handlers below

}
