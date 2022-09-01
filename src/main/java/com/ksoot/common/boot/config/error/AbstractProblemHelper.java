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

import static com.ksoot.common.boot.config.error.ProblemConstants.Keys.STACKTRACE;

import java.net.URI;

import org.springframework.util.ClassUtils;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.ThrowableProblem;

import com.ksoot.common.error.ProblemUtil;
import com.ksoot.common.error.resolver.ErrorResolver;
import com.ksoot.common.error.resolver.GeneralErrorResolver;

import lombok.NonNull;

/**
 * @author Rajveer Singh
 */
public abstract class AbstractProblemHelper {

	@NonNull
	protected ProblemProperties properties;

	protected ErrorBuilder errorBuilder;

	protected AbstractProblemHelper(final ErrorBuilder errorBuilder, final ProblemProperties properties) {
		this.errorBuilder = errorBuilder;
		this.properties = properties;
		// Problem.DEFAULT_TYPE
	}

	public Problem problemInstance(final Throwable throwable, final URI requestUri) {
		ErrorResolver errorResolver = GeneralErrorResolver.INTERNAL_SERVER_ERROR;
		ProblemBuilder problemBuilder = this.errorBuilder.problemBuilder(errorResolver, requestUri);
		addDebugInfo(problemBuilder, throwable);
		return problemBuilder.build();
	}

	protected Problem problemInstance(final Exception exception, ErrorResolver errorResolver, final URI requestUri) {
		ProblemBuilder problemBuilder = this.errorBuilder.problemBuilder(errorResolver, requestUri);
		addDebugInfo(problemBuilder, exception);
		return problemBuilder.build();
	}

	// Add details, cause and stacktrace if enabled
	public void addDebugInfo(ProblemBuilder problemBuilder, final Throwable exception) {
		if (this.properties.isDebugInfo()) {
			problemBuilder.withDetail(
					exception instanceof Problem p ? ((Problem) exception).getDetail() : exception.getMessage());
			if (exception.getCause() != null) {
				if (ClassUtils.isAssignableValue(ThrowableProblem.class, exception.getCause())) {
					problemBuilder.withCause((ThrowableProblem) exception.getCause());
				} else {
					problemBuilder.withCause(ProblemUtil.toProblem(exception));
				}
			}

			problemBuilder.with(STACKTRACE, ProblemUtil.getStackTrace(exception));
		}
	}

}
