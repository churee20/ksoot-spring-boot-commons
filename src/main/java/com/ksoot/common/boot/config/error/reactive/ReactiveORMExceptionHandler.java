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
import static com.ksoot.common.boot.config.error.ProblemConstants.Keys.LOCALIZED_MESSAGE;
import static com.ksoot.common.boot.config.error.ProblemConstants.Keys.MESSAGE;
import static com.ksoot.common.boot.config.error.ProblemConstants.Keys.TIMESTAMP;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.spring.webflux.advice.ProblemHandling;

import com.ksoot.common.boot.config.error.ProblemProperties;
import com.ksoot.common.boot.config.error.db.ConstraintNameResolver;
import com.ksoot.common.error.resolver.GeneralErrorResolver;
import com.ksoot.common.message.MessageProvider;

import reactor.core.publisher.Mono;

/**
 * @author Rajveer Singh
 */
@Configuration(value = APPLICATION_EXCEPTION_HANDLER_BEAN_NAME)
@EnableConfigurationProperties(ProblemProperties.class)
@ConditionalOnProperty(prefix = "application.problem", name = "enabled", havingValue = "true")
@ConditionalOnClass(value = { ProblemHandling.class, Database.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnMissingBean(name = APPLICATION_EXCEPTION_HANDLER_BEAN_NAME)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ReactiveORMExceptionHandler extends AbstractAdviceTrait {

	private static final String DATA_INTEGRITY_VIOLATION_EXCEPTION_PREFIX = "data.integrity.violation.";

	private ReactiveProblemHelper problemHelper;

	private ConstraintNameResolver constraintNameResolver;

	public ReactiveORMExceptionHandler(final ReactiveProblemHelper problemHelper,
			@Nullable final ConstraintNameResolver constraintNameResolver) {
		this.problemHelper = problemHelper;
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public Mono<ResponseEntity<Problem>> handleDataIntegrityViolationException(final DataIntegrityViolationException exception,
			final ServerWebExchange request) {
		if(this.constraintNameResolver == null) {
			return create(exception, this.problemHelper.problemInstance(exception, this.problemHelper.requestUri(request)), request);
		} else {
			GeneralErrorResolver errorResolver = GeneralErrorResolver.DATA_INTEGRITY_VIOLATION_EXCEPTION;
	        String exMessage = exception.getMostSpecificCause().getMessage().trim();
	        String constraintName = this.constraintNameResolver.resolveConstraintName(exception);
			ProblemBuilder problemBuilder = Problem.builder()
					.withInstance(this.problemHelper.requestUri(request))
					.withStatus(errorResolver.status())
					.with(MESSAGE, MessageProvider.getMessage(DATA_INTEGRITY_VIOLATION_EXCEPTION_PREFIX + constraintName, exMessage))
					.with(LOCALIZED_MESSAGE,
							MessageProvider.getMessage(DATA_INTEGRITY_VIOLATION_EXCEPTION_PREFIX + constraintName, exMessage))
					.with(TIMESTAMP, this.problemHelper.time());
			this.problemHelper.addDebugInfo(problemBuilder, exception);
			return create(exception, problemBuilder.build(), request);
		}
	}

	@ExceptionHandler(ConcurrencyFailureException.class)
	public Mono<ResponseEntity<Problem>> handleConcurrencyFailureException(final ConcurrencyFailureException exception,
			final ServerWebExchange request) {
		return this.problemHelper.createProblem(exception, GeneralErrorResolver.CONCURRENCY_FAILURE_EXCEPTION,
				request);
	}

	//TODO: Add more ORM exception handlers
}
