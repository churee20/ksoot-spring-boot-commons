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

package com.ksoot.common.error;

import static java.util.Arrays.asList;
import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;

import java.net.URI;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;
import org.zalando.problem.StatusType;
import org.zalando.problem.ThrowableProblem;

/**
 * @author Rajveer Singh
 */
public class ProblemUtil {

	public static ThrowableProblem toProblem(final Throwable throwable) {
		final StatusType status = Optional.ofNullable(resolveResponseStatus(throwable))
				.<StatusType>map(ResponseStatusAdapter::new).orElse(Status.INTERNAL_SERVER_ERROR);

		return toProblem(throwable, status);
	}

	public static ResponseStatus resolveResponseStatus(final Throwable type) {
		@Nullable
		final ResponseStatus candidate = findMergedAnnotation(type.getClass(), ResponseStatus.class);
		return candidate == null && type.getCause() != null ? resolveResponseStatus(type.getCause()) : candidate;
	}

	public static ThrowableProblem toProblem(final Throwable throwable, final StatusType status) {
		return toProblem(throwable, status, Problem.DEFAULT_TYPE);
	}

	public static ThrowableProblem toProblem(final Throwable throwable, final StatusType status, final URI type) {
		final ThrowableProblem problem = prepare(throwable, status, type).build();
		final StackTraceElement[] stackTrace = createStackTrace(throwable);
		problem.setStackTrace(stackTrace);
		return problem;
	}

	private static ProblemBuilder prepare(final Throwable throwable, final StatusType status, final URI type) {
		return Problem.builder().withType(type).withTitle(status.getReasonPhrase()).withStatus(status)
				.withDetail(throwable.getMessage())
				.withCause(Optional.ofNullable(throwable.getCause()).map(ProblemUtil::toProblem).orElse(null));
	}

	private static StackTraceElement[] createStackTrace(final Throwable throwable) {
		final Throwable cause = throwable.getCause();

		if (cause == null) {
			return throwable.getStackTrace();
		}
		else {

			final StackTraceElement[] next = cause.getStackTrace();
			final StackTraceElement[] current = throwable.getStackTrace();

			final int length = current.length - lengthOfTrailingPartialSubList(asList(next), asList(current));
			final StackTraceElement[] stackTrace = new StackTraceElement[length];
			System.arraycopy(current, 0, stackTrace, 0, length);
			return stackTrace;
		}
	}

	private static int lengthOfTrailingPartialSubList(final List<?> source, final List<?> target) {
		final int s = source.size() - 1;
		final int t = target.size() - 1;
		int l = 0;

		while (l <= s && l <= t && source.get(s - l).equals(target.get(t - l))) {
			l++;
		}

		return l;
	}

	public static String getStackTrace(final Throwable exception) {
		String stacktrace = ExceptionUtils.getStackTrace(exception);
		StringBuilder escapedStacktrace = new StringBuilder(stacktrace.length() + 100);
		StringCharacterIterator scitr = new StringCharacterIterator(stacktrace);

		char current = scitr.first();
		// DONE = \\uffff (not a character)
		String lastAppend = null;
		while (current != CharacterIterator.DONE) {
			if (current == '\t' || current == '\r' || current == '\n') {
				if (!" ".equals(lastAppend)) {
					escapedStacktrace.append(" ");
					lastAppend = " ";
				}
			}
			else {
				// nothing matched - just text as it is.
				escapedStacktrace.append(current);
				lastAppend = "" + current;
			}
			current = scitr.next();
		}
		return escapedStacktrace.toString();
	}

	private static class ResponseStatusAdapter implements StatusType {

		private final ResponseStatus status;

		ResponseStatusAdapter(final ResponseStatus status) {
			this.status = status;
		}

		@Override
		public int getStatusCode() {
			return this.status.code().value();
		}

		@Override
		public String getReasonPhrase() {
			return this.status.reason().isEmpty() ? this.status.value().getReasonPhrase() : this.status.reason();
		}

	}

}
