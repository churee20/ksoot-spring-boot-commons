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

import java.net.URI;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;
import org.zalando.problem.spring.webflux.advice.AdviceTrait;

import com.ksoot.common.boot.config.error.AbstractProblemHelper;
import com.ksoot.common.boot.config.error.ErrorBuilder;
import com.ksoot.common.boot.config.error.FieldErrorVM;
import com.ksoot.common.boot.config.error.ProblemProperties;
import com.ksoot.common.boot.config.error.ViolationVM;
import com.ksoot.common.error.ApplicationException;
import com.ksoot.common.error.ApplicationProblem;
import com.ksoot.common.error.resolver.ErrorResolver;
import com.ksoot.common.error.resolver.GeneralErrorResolver;

import reactor.core.publisher.Mono;

/**
 * @author Rajveer Singh
 */
public class ReactiveProblemHelper extends AbstractProblemHelper implements AdviceTrait {

	public ReactiveProblemHelper(final ErrorBuilder errorBuilder, final ProblemProperties properties) {
		super(errorBuilder, properties);
	}

	Mono<ResponseEntity<Problem>> createProblem(final ErrorResolver errorResolver, final Throwable exception,
			final ServerWebExchange request) {
		ProblemBuilder problemBuilder = this.errorBuilder.problemBuilder(errorResolver, requestUri(request));
		addDebugInfo(problemBuilder, exception);
		return create(exception, problemBuilder.build(), request);
	}

	Mono<ResponseEntity<Problem>> createProblem(final ApplicationProblem problem, final ServerWebExchange request) {
		ErrorResolver errorResolver = problem.getErrorResolver();
		ProblemBuilder problemBuilder = errorResolver != null
				? this.errorBuilder.problemBuilder(errorResolver, requestUri(request))
				: this.errorBuilder.problemBuilder(problem.getTitle(), requestUri(request), problem.getType(),
						problem.getStatus(), problem.getMessage(), problem.getLocalizedMessage());
		addDebugInfo(problemBuilder, problem);
		return create(problem, problemBuilder.build(), request);
	}

	Mono<ResponseEntity<Problem>> createProblem(final ApplicationException exception, final ServerWebExchange request) {
		ErrorResolver errorResolver = exception.getErrorResolver();
		ProblemBuilder problemBuilder = this.errorBuilder.problemBuilder(errorResolver, requestUri(request));
		addDebugInfo(problemBuilder, exception);
		return create(exception, problemBuilder.build(), request);
	}

	Mono<ResponseEntity<Problem>> createProblem(final ConstraintViolationException exception,
			final ServerWebExchange request) {
		ErrorResolver errorResolver = GeneralErrorResolver.CONSTRAINT_VIOLATIONS;
		ProblemBuilder problemBuilder = this.errorBuilder.problemBuilder(errorResolver, requestUri(request));
		List<ViolationVM> violations = exception.getConstraintViolations().stream()
				.map(voilation -> ViolationVM.of(properties, voilation)).toList();
		this.errorBuilder.addViolations(problemBuilder, violations);
		addDebugInfo(problemBuilder, exception);
		return create(exception, problemBuilder.build(), request);
	}

	Mono<ResponseEntity<Problem>> createProblem(final WebExchangeBindException exception,
			final ServerWebExchange request) {
		ErrorResolver errorResolver = GeneralErrorResolver.VALIDATION_FAILURE;
		ProblemBuilder problemBuilder = this.errorBuilder.problemBuilder(errorResolver, requestUri(request));

		BindingResult result = exception.getBindingResult();
		List<FieldErrorVM> fieldErrors = result.getFieldErrors().stream()
				.map(fieldError -> FieldErrorVM.of(properties, fieldError)).toList();

		this.errorBuilder.addFieldErrors(problemBuilder, fieldErrors);
		addDebugInfo(problemBuilder, exception);
		return create(exception, problemBuilder.build(), request);
	}

	Mono<ResponseEntity<Problem>> createProblem(final Throwable throwable, final ServerWebExchange request) {
		return create(throwable, problemInstance(throwable, requestUri(request)), request);
	}

	Mono<ResponseEntity<Problem>> createProblem(final Exception exception, ErrorResolver errorResolver,
			final ServerWebExchange request) {
		return create(exception, problemInstance(exception, errorResolver, requestUri(request)), request);
	}

	Mono<ResponseEntity<Problem>> createProblem(final ResponseStatusException exception,
			final ServerWebExchange request) {
		ErrorResolver errorResolver = GeneralErrorResolver.RESPONSE_STATUS_EXCEPTION;
		URI requestUri = requestUri(request);
		ProblemBuilder problemBuilder = this.errorBuilder.problemBuilder(errorResolver.title(), requestUri,
				this.errorBuilder.generateType(requestUri, errorResolver.typeCode()),
				Status.valueOf(exception.getRawStatusCode()), errorResolver.message(),
				errorResolver.localizedMessage());
		addDebugInfo(problemBuilder, exception);
		return create(exception, problemBuilder.build(), request);
	}

	URI requestUri(final ServerWebExchange request) {
//		return request.getRequest().getURI();
		return URI.create(request.getRequest().getPath().pathWithinApplication().toString());
	}

}
