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
public enum GeneralTitleMessageResolver implements MessageResolver {

	// @formatter:off
	// --------------------- Standard ------------------------------
	BAD_REQUEST(TITLE_MESSAGE_CODE_PREFIX + ".bad.request", "Bad Request"),  // 400
	UNAUTHORIZED(TITLE_MESSAGE_CODE_PREFIX + ".unauthorized", "Unauthorized"),  // 401
	PAYMENT_REQUIRED(TITLE_MESSAGE_CODE_PREFIX + ".payment.required", "Payment Required"),  // 402
	FORBIDDEN(TITLE_MESSAGE_CODE_PREFIX + ".forbidden", "Forbidden"),  // 403
	NOT_FOUND(TITLE_MESSAGE_CODE_PREFIX + ".not.found", "Not Found"),  // 404
	METHOD_NOT_ALLOWED(TITLE_MESSAGE_CODE_PREFIX + ".method.not.allowed", "Method Not Allowed"),  // 405
	NOT_ACCEPTABLE(TITLE_MESSAGE_CODE_PREFIX + ".not.acceptable", "Not Acceptable"),  //406
	PROXY_AUTHENTICATION_REQUIRED(TITLE_MESSAGE_CODE_PREFIX + ".proxy.authentication.required", "Proxy Authentication Required"),  // 407
	REQUEST_TIMEOUT(TITLE_MESSAGE_CODE_PREFIX + ".request.timeout", "Request Timeout"),  // 408
	CONFLICT(TITLE_MESSAGE_CODE_PREFIX + ".conflict", "Conflict"),  // 409
	GONE(TITLE_MESSAGE_CODE_PREFIX + ".gone", "Gone"),  // 410
	LENGTH_REQUIRED(TITLE_MESSAGE_CODE_PREFIX + ".length.required", "Length Required"),  // 411
	PRECONDITION_FAILED(TITLE_MESSAGE_CODE_PREFIX + ".precondition.failed", "Precondition Failed"),  // 412
	REQUEST_ENTITY_TOO_LARGE(TITLE_MESSAGE_CODE_PREFIX + ".request.entity.too.large", "Request Entity Too Large"),  // 413
	REQUEST_URI_TOO_LONG(TITLE_MESSAGE_CODE_PREFIX + ".request.uri.too.long", "Request-URI Too Long"),  // 414
	UNSUPPORTED_MEDIA_TYPE(TITLE_MESSAGE_CODE_PREFIX + ".unsupported.media.type", "Unsupported Media Type"),  // 415
	REQUESTED_RANGE_NOT_SATISFIABLE(TITLE_MESSAGE_CODE_PREFIX + ".requested.range.not.satisfiable", "Requested Range Not Satisfiable"),  // 41
	EXPECTATION_FAILED(TITLE_MESSAGE_CODE_PREFIX + ".expectation.failed", "Expectation Failed"),  // 417
	I_AM_A_TEAPOT(TITLE_MESSAGE_CODE_PREFIX + ".i.am.a.teapot", "I'm a teapot"),  // 418
	UNPROCESSABLE_ENTITY(TITLE_MESSAGE_CODE_PREFIX + ".unprocessable.entity", "Unprocessable Entity"),  // 422
	LOCKED(TITLE_MESSAGE_CODE_PREFIX + ".locked", "Locked"),  // 423
	FAILED_DEPENDENCY(TITLE_MESSAGE_CODE_PREFIX + ".failed.dependency", "Failed Dependency"),  // 424
	UPGRADE_REQUIRED(TITLE_MESSAGE_CODE_PREFIX + ".upgrade.required", "Upgrade Required"),  // 426
	PRECONDITION_REQUIRED(TITLE_MESSAGE_CODE_PREFIX + ".precondition.required", "Precondition Required"),  // 428
	TOO_MANY_REQUESTS(TITLE_MESSAGE_CODE_PREFIX + ".too.many.requests", "Too Many Requests"),  // 429
	REQUEST_HEADER_FIELDS_TOO_LARGE(TITLE_MESSAGE_CODE_PREFIX + ".request.header.fields.too.large", "Request Header Fields Too Large"), // 431
	UNAVAILABLE_FOR_LEGAL_REASONS(TITLE_MESSAGE_CODE_PREFIX + ".unavailable.for.legal.reasons", "Unavailable For Legal Reasons"), // 451
	INTERNAL_SERVER_ERROR(TITLE_MESSAGE_CODE_PREFIX + ".internal.server.error", "Internal Server Error"), // 500
	NOT_IMPLEMENTED(TITLE_MESSAGE_CODE_PREFIX + ".not.implemented", "Not Implemented"), // 501
	BAD_GATEWAY(TITLE_MESSAGE_CODE_PREFIX + ".bad.gateway", "Bad Gateway"),  // 502
	SERVICE_UNAVAILABLE(TITLE_MESSAGE_CODE_PREFIX + ".service.unavailable", "Service Unavailable"), // 503
	GATEWAY_TIMEOUT(TITLE_MESSAGE_CODE_PREFIX + ".gateway.timeout", "Gateway Timeout"),  // 504
	HTTP_VERSION_NOT_SUPPORTED(TITLE_MESSAGE_CODE_PREFIX + ".http.version.not.supported", "HTTP Version Not Supported"),  // 505
	VARIANT_ALSO_NEGOTIATES(TITLE_MESSAGE_CODE_PREFIX + ".variant.also.negotiates", "Variant Also Negotiates"),  // 506
	INSUFFICIENT_STORAGE(TITLE_MESSAGE_CODE_PREFIX + ".insufficient.storage", "Insufficient Storage"),  // 507
	LOOP_DETECTED(TITLE_MESSAGE_CODE_PREFIX + ".loop.detected", "Loop Detected"),  // 508
	BANDWIDTH_LIMIT_EXCEEDED(TITLE_MESSAGE_CODE_PREFIX + ".bandwidth.limit.exceeded", "Bandwidth Limit Exceeded"),  // 509
	NOT_EXTENDED(TITLE_MESSAGE_CODE_PREFIX + ".not.extended", "Not Extended"),  // 510
	NETWORK_AUTHENTICATION_REQUIRED(TITLE_MESSAGE_CODE_PREFIX + ".network.authentication.required", "Network Authentication Required"), // 511

	// ----------------------------- Custom ---------------------------
	RESPONSE_STATUS_ERROR(TITLE_MESSAGE_CODE_PREFIX + ".response.status.error", "Response status error"),
	CONSTRAINT_VIOLATIONS(TITLE_MESSAGE_CODE_PREFIX + ".constraint.violations", "Constraint Violations"),  // 400
	VALIDATION_FAILURE(TITLE_MESSAGE_CODE_PREFIX + ".validation.failure", "Validation Failure"),  // 400
	SECURITY_ERROR(TITLE_MESSAGE_CODE_PREFIX + ".security.error", "Security Error"),  // 400
	CONCURRENCY_FAILURE(TITLE_MESSAGE_CODE_PREFIX + ".concurrency.failure", "Concurrency Failure"),
	DATA_INTEGRITY_VIOLATION_EXCEPTION(TITLE_MESSAGE_CODE_PREFIX + ".data.integrity.violation.exception", "Database constraint voilated"),
	CONCURRENCY_FAILURE_EXCEPTION(TITLE_MESSAGE_CODE_PREFIX + ".concurrency.failure.exception", "Concurrent locking failure");
	// @formatter:on

	private String messageCode;

	private String defaultMessage;

	private GeneralTitleMessageResolver(final String messageCode, final String defaultMessage) {
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
