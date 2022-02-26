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

package com.ksoot.common.error.resolver;

import org.zalando.problem.Status;
import org.zalando.problem.StatusType;

import com.ksoot.common.message.MessageResolver;

/**
 * @author Rajveer Singh
 */
public interface ErrorResolver {

	String TYPE_INTERNAL_SERVER_ERROR = "internal-server-error";

	String TYPE_CONSTRAINT_VIOLATIONS = "constraint-violations";

	String TYPE_DATA_INTEGRITY_VIOLATION = "data-integrity-violation";

	String TYPE_CONCURRENCY_FAILURE = "concurrency-failure";

	String TYPE_VALIDATION_FAILURE = "validation-failure";

	String TYPE_NOT_FOUND = "not-found";

	String TYPE_BAD_REQUEST = "bad-request";

	public String message();

	public String localizedMessage();

	public String typeCode();

	public default StatusType status() {
		return Status.INTERNAL_SERVER_ERROR;
	}

	public String title();

	public ErrorResolver parameters(final Object... params);

	public ErrorResolver status(final StatusType status);

	public ErrorResolver titleResolver(final MessageResolver titleResolver);

}
