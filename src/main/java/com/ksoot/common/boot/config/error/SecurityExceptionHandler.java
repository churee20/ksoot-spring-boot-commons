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
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait;

import com.ksoot.common.error.resolver.GeneralErrorResolver;

/**
 * @author Rajveer Singh
 */
@Configuration
@EnableConfigurationProperties(ProblemProperties.class)
@Conditional(SecurityExceptionAdviceConfigurationCondition.class)
@ConditionalOnClass(value = { WebSecurityConfiguration.class, ProblemHandling.class })
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class SecurityExceptionHandler extends AbstractAdviceTrait implements SecurityAdviceTrait {

	private ProblemHelper problemHelper;

	public SecurityExceptionHandler(final ProblemHelper problemHelper) {
		this.problemHelper = problemHelper;
	}

	@Override
	public ResponseEntity<Problem> handleAccessDenied(final AccessDeniedException exception, final NativeWebRequest request) {
		return this.problemHelper.createProblem(GeneralErrorResolver.ACCESS_DENIED, exception, request);
	}

	@Override
	public ResponseEntity<Problem> handleAuthentication(final AuthenticationException exception, final NativeWebRequest request) {
		return this.problemHelper.createProblem(GeneralErrorResolver.AUTHENTICATION_EXCEPTION, exception, request);
	}

	@ExceptionHandler(InsufficientAuthenticationException.class)
	protected ResponseEntity<Problem> handleInsufficientAuthenticationException(
			final InsufficientAuthenticationException exception, final NativeWebRequest request) {
		return this.problemHelper.createProblem(GeneralErrorResolver.INSUFFICIENT_AUTHENTICATION, exception, request);
	}

}
