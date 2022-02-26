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

import com.ksoot.common.message.MessageResolver;

/**
 * @author Rajveer Singh
 */
public enum GeneralErrorMessageResolver implements MessageResolver {

	// @formatter:off
	INTERNAL_SERVER_ERROR(ERROR_MESSAGE_CODE_PREFIX + ".internal.server.error", "Something has gone wrong, please contact administrator"),
	BAD_REQUEST(ERROR_MESSAGE_CODE_PREFIX + ".bad.request", "Invalid input"),
	DUPLICATE_CORRELATION_ID(ERROR_MESSAGE_CODE_PREFIX + ".duplicate.correlation.id", "Duplicate Correlation Id"),
	BAD_JWT(ERROR_MESSAGE_CODE_PREFIX + ".bad.jwt", "An error has occurred while attempting to decode the Jwt: {0}"),
	CONSTRAINT_VIOLATIONS(ERROR_MESSAGE_CODE_PREFIX + ".constraint.violations", "Constraint Violations"),
	VALIDATION_FAILURE(ERROR_MESSAGE_CODE_PREFIX + ".validation.failure", "Validation Failure"),
	DATA_INTEGRITY_VIOLATION_EXCEPTION(ERROR_MESSAGE_CODE_PREFIX + ".data.integrity.violation.exception", "Database constraint voilated"),
	CONCURRENCY_FAILURE_EXCEPTION(ERROR_MESSAGE_CODE_PREFIX + ".concurrency.failure.exception", "Concurrent locking failure"),
	FORBIDDEN(ERROR_MESSAGE_CODE_PREFIX + ".forbidden", "Forbidden"),
	NOT_FOUND(ERROR_MESSAGE_CODE_PREFIX + ".not.found", "No such record exists"),
	FEATURE_NOT_ENABLED(ERROR_MESSAGE_CODE_PREFIX + ".feature.not.enabled", "Feature {0} is not enabled, please contact system administrator"),

	NOT_IMPLEMENTED(ERROR_MESSAGE_CODE_PREFIX + ".not.implemented", "Unsupported Operation error"), // 501
	METHOD_NOT_ALLOWED(ERROR_MESSAGE_CODE_PREFIX + ".method.not.allowed", "Method Not Allowed"),  // 405
	NOT_ACCEPTABLE(ERROR_MESSAGE_CODE_PREFIX + ".not.acceptable", "Not Acceptable"),  //406
	UNSUPPORTED_MEDIA_TYPE(ERROR_MESSAGE_CODE_PREFIX + ".unsupported.media.type", "Unsupported Media Type"),  // 415
	RESPONSE_STATUS_ERROR(ERROR_MESSAGE_CODE_PREFIX + ".response.status.error", "Response status error"),
	GATEWAY_TIMEOUT(ERROR_MESSAGE_CODE_PREFIX + ".gateway.timeout", "Gateway Timeout"),  // 504

	AUTHENTICATION_EXCEPTION(ERROR_MESSAGE_CODE_PREFIX + ".authentication.error", "Authentication exception"),
	INSUFFICIENT_AUTHENTICATION(ERROR_MESSAGE_CODE_PREFIX + ".insufficient.authentication.exception", "Authentication required to access the resource"),
	TOKEN_NOT_ACTIVE(ERROR_MESSAGE_CODE_PREFIX + ".token.not.active.exception", "Security token has expired, please login again"),
	ACCESS_DENIED(ERROR_MESSAGE_CODE_PREFIX + ".access.denied", "Unauthorized access not allowed"),
	INVALID_CREDENTIALS(ERROR_MESSAGE_CODE_PREFIX + ".invalid.credentials", "Invalid credentials"),
	INCORRECT_PASSWORD(ERROR_MESSAGE_CODE_PREFIX + ".incorrect.password", "Incorrrect password"),

	CONCURRENCY_FAILURE(ERROR_MESSAGE_CODE_PREFIX + ".concurrency.failure", "Concurrency Failure"),
	PRECONDITION_REQUIRED(ERROR_MESSAGE_CODE_PREFIX + ".precondition.required", "Precondition Required"),
	PRECONDITION_FAILED(ERROR_MESSAGE_CODE_PREFIX + ".precondition.failed", "Precondition Failed"),
	TOO_MANY_REQUESTS(ERROR_MESSAGE_CODE_PREFIX + ".too.many.requests", "Too Many Requests"),
	CONFLICT(ERROR_MESSAGE_CODE_PREFIX + ".conflict", "Conflict"),
	EMPTY_REQUEST(ERROR_MESSAGE_CODE_PREFIX + ".empty.request", "Request is empty");
	// @formatter:on

	private String messageCode;

	private String defaultMessage;

	private GeneralErrorMessageResolver(final String messageCode, final String defaultMessage) {
		this.messageCode = messageCode;
		this.defaultMessage = defaultMessage;
	}

	@Override
	public String messageCode() {
		return this.messageCode;
	}

	@Override
	public String defaultMessage() {
		return this.defaultMessage;
	}

}
