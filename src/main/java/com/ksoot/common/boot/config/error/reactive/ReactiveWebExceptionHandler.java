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

import static com.ksoot.common.boot.BootConstant.BeanName.WEB_EXCEPTION_HANDLER_BEAN_NAME;

import java.net.SocketTimeoutException;

import javax.validation.ConstraintViolationException;

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
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;
import org.zalando.problem.spring.webflux.advice.ProblemHandling;

import com.ksoot.common.boot.config.error.ProblemProperties;
import com.ksoot.common.error.resolver.GeneralErrorResolver;

import reactor.core.publisher.Mono;

/**
 * @author Rajveer Singh
 */
@Configuration(value = WEB_EXCEPTION_HANDLER_BEAN_NAME)
@EnableConfigurationProperties(ProblemProperties.class)
@ConditionalOnProperty(prefix = "application.problem", name = "enabled", havingValue = "true")
@ConditionalOnClass(ProblemHandling.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Order(value = Ordered.LOWEST_PRECEDENCE)
@ControllerAdvice
public class ReactiveWebExceptionHandler extends AbstractAdviceTrait implements ProblemHandling {

	private ReactiveProblemHelper problemHelper;

	public ReactiveWebExceptionHandler(final ReactiveProblemHelper problemHelper) {
		this.problemHelper = problemHelper;
	}

	@Override
	public Mono<ResponseEntity<Problem>> handleProblem(final ThrowableProblem problem, final ServerWebExchange request) {
		return create(problem, request);
	}

	@Override
	public Mono<ResponseEntity<Problem>> handleThrowable(final Throwable throwable, final ServerWebExchange request) {
		return this.problemHelper.createProblem(throwable, request);
	}

	@Override
	public Mono<ResponseEntity<Problem>> handleConstraintViolation(final ConstraintViolationException exception,
			final ServerWebExchange request) {
		return this.problemHelper.createProblem(exception, request);
	}

	@Override
	public Mono<ResponseEntity<Problem>> handleBindingResult(final WebExchangeBindException exception,
			final ServerWebExchange request) {
		return this.problemHelper.createProblem(exception, request);
	}

	@Override
	public Mono<ResponseEntity<Problem>> handleUnsupportedOperation(final UnsupportedOperationException exception,
			final ServerWebExchange request) {
		return this.problemHelper.createProblem(exception, GeneralErrorResolver.UNSUPPORTED_OPERATION_EXCEPTION,
				request);
	}

	@Override
	public Mono<ResponseEntity<Problem>> handleRequestMethodNotSupportedException(final MethodNotAllowedException exception,
			final ServerWebExchange request) {
		return this.problemHelper.createProblem(exception, GeneralErrorResolver.METHOD_NOT_ALLOWED_EXCEPTION, request);
	}

	@Override
	public Mono<ResponseEntity<Problem>> handleMediaTypeNotAcceptable(final NotAcceptableStatusException exception,
			final ServerWebExchange request) {
		return this.problemHelper.createProblem(exception, GeneralErrorResolver.NOT_ACCEPTABLE_STATUS_EXCEPTION,
				request);
	}

	@Override
	public Mono<ResponseEntity<Problem>> handleMediaTypeNotSupportedException(
			final UnsupportedMediaTypeStatusException exception, final ServerWebExchange request) {
		return this.problemHelper.createProblem(exception, GeneralErrorResolver.UNSUPPORTED_MEDIA_TYPE_STATUS_EXCEPTION,
				request);
	}

	@Override
	public Mono<ResponseEntity<Problem>> handleResponseStatusException(final ResponseStatusException exception,
			final ServerWebExchange request) {
		return this.problemHelper.createProblem(exception, request);
	}

	@Override
	public Mono<ResponseEntity<Problem>> handleSocketTimeout(final SocketTimeoutException exception,
			final ServerWebExchange request) {
		return this.problemHelper.createProblem(exception, GeneralErrorResolver.SOCKET_TIMEOUT_EXCEPTION, request);
	}

}
