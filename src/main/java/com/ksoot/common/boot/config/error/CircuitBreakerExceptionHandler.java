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
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.spring.web.advice.network.CircuitBreakerOpenAdviceTrait;

import net.jodah.failsafe.CircuitBreakerOpenException;

/**
 * @author Rajveer Singh
 */
@Configuration
@ConditionalOnClass(value = { ProblemHandling.class })
@EnableConfigurationProperties(ProblemProperties.class)
@ConditionalOnProperty(prefix = "application.problem", name = "enabled", havingValue = "true")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class CircuitBreakerExceptionHandler extends AbstractAdviceTrait implements CircuitBreakerOpenAdviceTrait  {

	// private ProblemHelper problemHelper;
	//
	// public CircuitBreakerExceptionHandler(ProblemHelper problemHelper) {
	// this.problemHelper = problemHelper;
	// }

	/*
	 * (non-Javadoc)
	 *
	 * @see org.zalando.problem.spring.web.advice.network.CircuitBreakerOpenAdviceTrait#
	 * handleCircuitBreakerOpen(net.jodah.failsafe.CircuitBreakerOpenException,
	 * org.springframework.web.context.request.NativeWebRequest)
	 */
	@Override
	public ResponseEntity<Problem> handleCircuitBreakerOpen(final CircuitBreakerOpenException exception,
			final NativeWebRequest request) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Auto-generated method stub");
	}

}
