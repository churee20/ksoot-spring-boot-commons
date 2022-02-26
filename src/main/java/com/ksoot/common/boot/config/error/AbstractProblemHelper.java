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

import static com.ksoot.common.boot.config.error.ProblemConstants.ERROR_DECRIPTION_FILE;
import static com.ksoot.common.boot.config.error.ProblemConstants.Keys.LOCALIZED_MESSAGE;
import static com.ksoot.common.boot.config.error.ProblemConstants.Keys.MESSAGE;
import static com.ksoot.common.boot.config.error.ProblemConstants.Keys.STACKTRACE;
import static com.ksoot.common.boot.config.error.ProblemConstants.Keys.TIMESTAMP;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ClassUtils;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.StatusType;
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
	protected final String CONTEXT_PATH;

	@NonNull
	protected static ProblemProperties properties;

	protected AbstractProblemHelper(final String contextPath, final ProblemProperties properties) {
		this.CONTEXT_PATH = StringUtils.isEmpty(contextPath) ? "" : "/" + contextPath;
		AbstractProblemHelper.properties = properties;
		// Problem.DEFAULT_TYPE
	}

	public Problem problemInstance(final Throwable throwable, final URI requestUri) {
		ErrorResolver errorResolver = GeneralErrorResolver.INTERNAL_SERVER_ERROR;
		ProblemBuilder problemBuilder = problemBuilder(errorResolver, requestUri);
		problemBuilder.withType(generateType(requestUri, errorResolver.typeCode()));
		addDebugInfo(problemBuilder, throwable);
		return problemBuilder.build();
	}

	protected Problem problemInstance(final Exception exception, ErrorResolver errorResolver, final URI requestUri) {
		ProblemBuilder problemBuilder = problemBuilder(errorResolver, requestUri);
		problemBuilder.withType(generateType(requestUri, errorResolver.typeCode()));
		addDebugInfo(problemBuilder, exception);
		return problemBuilder.build();
	}

	protected ProblemBuilder problemBuilder(final ErrorResolver errorResolver, final URI requestUri) {
		return problemBuilder(errorResolver.title(),
				requestUri, errorResolver.status(), errorResolver.message(), 
				errorResolver.localizedMessage(), time());
	}
	
	// Defined problem structure, can be changed if required
	protected ProblemBuilder problemBuilder(final String title, 
			final URI instance, final StatusType status, final String message,
			final String localizedMessage, final OffsetDateTime time) {
		ProblemBuilder problemBuilder = Problem.builder()
				.withTitle(title).withInstance(instance)
				.withStatus(status).with(MESSAGE, message)
				.with(LOCALIZED_MESSAGE, localizedMessage).with(TIMESTAMP, time);
		return problemBuilder;
	}


	protected URI generateType(final URI requestUri, final String typeCode) {
		URI type = null;
		try {
			type = new URI(requestUri.getScheme(), requestUri.getAuthority(),
					this.CONTEXT_PATH + ERROR_DECRIPTION_FILE + "#" + typeCode, null, null);
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return type;
	}

	public OffsetDateTime time() {
		// return OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS);
		return OffsetDateTime.now();
	}

	// Add details, cause and stacktrace if enabled
	public void addDebugInfo(ProblemBuilder problemBuilder, final Throwable exception) {
		if (!AbstractProblemHelper.properties.isDebugInfo()) {
			return;
		}
		else {
			problemBuilder.withDetail(exception instanceof Problem ? ((Problem)exception).getDetail() : exception.getMessage());
			if (exception.getCause() != null) {
				if (ClassUtils.isAssignableValue(ThrowableProblem.class, exception.getCause())) {
					problemBuilder.withCause((ThrowableProblem) exception.getCause());
				}
				else {
					problemBuilder.withCause(ProblemUtil.toProblem(exception));
				}
			}

			problemBuilder.with(STACKTRACE, ProblemUtil.getStackTrace(exception));
		}
	}

}
