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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;
import org.zalando.problem.spring.web.advice.ProblemHandling;

import com.ksoot.common.error.resolver.GeneralErrorResolver;

/**
 * @author Rajveer Singh
 */
@Configuration(value = WEB_EXCEPTION_HANDLER_BEAN_NAME)
@EnableConfigurationProperties(ProblemProperties.class)
@ConditionalOnProperty(prefix = "application.problem", name = "enabled", havingValue = "true")
@ConditionalOnClass(ProblemHandling.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class WebExceptionHandler extends AbstractAdviceTrait implements ProblemHandling {

	private ProblemHelper problemHelper;

	public WebExceptionHandler(final ProblemHelper problemHelper) {
		this.problemHelper = problemHelper;
	}

	@Override
	public ResponseEntity<Problem> handleProblem(final ThrowableProblem problem, final NativeWebRequest request) {
		return create(problem, request);
	}

	@Override
	public ResponseEntity<Problem> handleThrowable(final Throwable throwable, final NativeWebRequest request) {
		return this.problemHelper.createProblem(throwable, request);
	}

	@Override
	public ResponseEntity<Problem> handleConstraintViolation(final ConstraintViolationException exception,
			final NativeWebRequest request) {
		return this.problemHelper.createProblem(exception, request);
	}

	@Override
	public ResponseEntity<Problem> handleMissingServletRequestParameter(
			final MissingServletRequestParameterException exception, final  NativeWebRequest request) {
		return this.problemHelper.createProblem(exception, request);
	}

	@Override
	public ResponseEntity<Problem> handleMethodArgumentNotValid(final MethodArgumentNotValidException exception,
			final  NativeWebRequest request) {
		return this.problemHelper.createProblem(exception, request);
	}

	@Override
	public ResponseEntity<Problem> handleUnsupportedOperation(final UnsupportedOperationException exception,
			final NativeWebRequest request) {
		return this.problemHelper.createProblem(exception, GeneralErrorResolver.UNSUPPORTED_OPERATION_EXCEPTION,
				request);
	}

	@Override
	public ResponseEntity<Problem> handleResponseStatusException(final ResponseStatusException exception,
			final NativeWebRequest request) {
		return this.problemHelper.createProblem(exception, request);
	}

	@Override
	public ResponseEntity<Problem> handleSocketTimeout(final SocketTimeoutException exception, 
			final NativeWebRequest request) {
		return this.problemHelper.createProblem(exception, GeneralErrorResolver.SOCKET_TIMEOUT_EXCEPTION, request);
	}
	
}
