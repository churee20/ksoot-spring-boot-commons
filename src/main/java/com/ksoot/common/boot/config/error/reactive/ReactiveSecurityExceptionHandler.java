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

import static com.ksoot.common.boot.BootConstant.BeanName.SECURITY_EXCEPTION_HANDLER_BEAN_NAME;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.webflux.advice.ProblemHandling;
import org.zalando.problem.spring.webflux.advice.security.SecurityAdviceTrait;

import com.ksoot.common.boot.config.error.ProblemProperties;
import com.ksoot.common.boot.config.error.SecurityExceptionAdviceConfigurationCondition;
import com.ksoot.common.error.resolver.GeneralErrorResolver;

import reactor.core.publisher.Mono;

/**
 * @author Rajveer Singh
 */
@Configuration(value = SECURITY_EXCEPTION_HANDLER_BEAN_NAME)
@EnableConfigurationProperties(ProblemProperties.class)
@Conditional(SecurityExceptionAdviceConfigurationCondition.class)
@ConditionalOnClass(value = { SecurityConfig.class, ProblemHandling.class })
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ReactiveSecurityExceptionHandler extends AbstractAdviceTrait implements SecurityAdviceTrait {

	private ReactiveProblemHelper problemHelper;

	public ReactiveSecurityExceptionHandler(final ReactiveProblemHelper problemHelper) {
		this.problemHelper = problemHelper;
	}

	@Override
	public Mono<ResponseEntity<Problem>> handleAccessDenied(final AccessDeniedException exception,
			final ServerWebExchange request) {
		return this.problemHelper.createProblem(GeneralErrorResolver.ACCESS_DENIED, exception, request);
	}

	@Override
	public Mono<ResponseEntity<Problem>> handleAuthentication(final AuthenticationException exception,
			final ServerWebExchange request) {
		return this.problemHelper.createProblem(GeneralErrorResolver.AUTHENTICATION_EXCEPTION, exception, request);
	}

	@ExceptionHandler(InsufficientAuthenticationException.class)
	protected Mono<ResponseEntity<Problem>> handleInsufficientAuthenticationException(
			final InsufficientAuthenticationException exception, final ServerWebExchange request) {
		return this.problemHelper.createProblem(GeneralErrorResolver.INSUFFICIENT_AUTHENTICATION, exception, request);
	}

}
