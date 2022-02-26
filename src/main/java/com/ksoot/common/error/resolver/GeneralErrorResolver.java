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

import com.ksoot.common.message.MessageProvider;
import com.ksoot.common.message.MessageResolver;

/**
 * @author Rajveer Singh
 */
public enum GeneralErrorResolver implements ErrorResolver {

	// @formatter:off
	INTERNAL_SERVER_ERROR(GeneralErrorMessageResolver.INTERNAL_SERVER_ERROR,
			TYPE_INTERNAL_SERVER_ERROR,
			GeneralTitleMessageResolver.INTERNAL_SERVER_ERROR,
			Status.INTERNAL_SERVER_ERROR),
	EMPTY_REQUEST(GeneralErrorMessageResolver.EMPTY_REQUEST,
			TYPE_BAD_REQUEST,
			GeneralTitleMessageResolver.BAD_REQUEST,
			Status.BAD_REQUEST),
	BAD_REQUEST(GeneralErrorMessageResolver.BAD_REQUEST,
			TYPE_BAD_REQUEST,
			GeneralTitleMessageResolver.BAD_REQUEST,
			Status.BAD_REQUEST),
	BAD_JWT(GeneralErrorMessageResolver.BAD_JWT,
			TYPE_BAD_REQUEST,
			GeneralTitleMessageResolver.BAD_REQUEST,
			Status.BAD_REQUEST),
	DUPLICATE_CORRELATION_ID(GeneralErrorMessageResolver.DUPLICATE_CORRELATION_ID,
			TYPE_BAD_REQUEST,
			GeneralTitleMessageResolver.BAD_REQUEST,
			Status.BAD_REQUEST),
	CONSTRAINT_VIOLATIONS(GeneralErrorMessageResolver.CONSTRAINT_VIOLATIONS,
			TYPE_CONSTRAINT_VIOLATIONS,
			GeneralTitleMessageResolver.CONSTRAINT_VIOLATIONS,
			Status.BAD_REQUEST),
	DATA_INTEGRITY_VIOLATION_EXCEPTION(GeneralErrorMessageResolver.DATA_INTEGRITY_VIOLATION_EXCEPTION,
			TYPE_DATA_INTEGRITY_VIOLATION,
			GeneralTitleMessageResolver.DATA_INTEGRITY_VIOLATION_EXCEPTION,
			Status.INTERNAL_SERVER_ERROR),
	CONCURRENCY_FAILURE_EXCEPTION(GeneralErrorMessageResolver.DATA_INTEGRITY_VIOLATION_EXCEPTION,
			TYPE_CONCURRENCY_FAILURE,
			GeneralTitleMessageResolver.DATA_INTEGRITY_VIOLATION_EXCEPTION,
			Status.INTERNAL_SERVER_ERROR),
	VALIDATION_FAILURE(GeneralErrorMessageResolver.VALIDATION_FAILURE,
			TYPE_VALIDATION_FAILURE,
			GeneralTitleMessageResolver.VALIDATION_FAILURE,
			Status.BAD_REQUEST),
	UNSUPPORTED_OPERATION_EXCEPTION(GeneralErrorMessageResolver.NOT_IMPLEMENTED,
			"unsupported-operation-exception",
			GeneralTitleMessageResolver.NOT_IMPLEMENTED,
			Status.NOT_IMPLEMENTED),
	METHOD_NOT_ALLOWED_EXCEPTION(GeneralErrorMessageResolver.METHOD_NOT_ALLOWED,
			"method-not-allowed-exception",
			GeneralTitleMessageResolver.METHOD_NOT_ALLOWED,
			Status.METHOD_NOT_ALLOWED),
	NOT_ACCEPTABLE_STATUS_EXCEPTION(GeneralErrorMessageResolver.NOT_ACCEPTABLE,
			"not-acceptable-status-exception",
			GeneralTitleMessageResolver.NOT_ACCEPTABLE,
			Status.NOT_ACCEPTABLE),
	UNSUPPORTED_MEDIA_TYPE_STATUS_EXCEPTION(GeneralErrorMessageResolver.UNSUPPORTED_MEDIA_TYPE,
			"unsupported-media-type-status-exception",
			GeneralTitleMessageResolver.UNSUPPORTED_MEDIA_TYPE,
			Status.UNSUPPORTED_MEDIA_TYPE),
	RESPONSE_STATUS_EXCEPTION(GeneralErrorMessageResolver.RESPONSE_STATUS_ERROR,
			"response-status-exception",
			GeneralTitleMessageResolver.RESPONSE_STATUS_ERROR,
			null),
	SOCKET_TIMEOUT_EXCEPTION(GeneralErrorMessageResolver.GATEWAY_TIMEOUT,
			"socket-timeout-exception",
			GeneralTitleMessageResolver.GATEWAY_TIMEOUT,
			Status.GATEWAY_TIMEOUT),

	INSUFFICIENT_AUTHENTICATION(GeneralErrorMessageResolver.INSUFFICIENT_AUTHENTICATION,
			"insufficient-authentication-exception",
			GeneralTitleMessageResolver.SECURITY_ERROR,
			Status.UNAUTHORIZED),
	ACCESS_DENIED(GeneralErrorMessageResolver.ACCESS_DENIED,
			"access-denied-exception",
			GeneralTitleMessageResolver.SECURITY_ERROR,
			Status.FORBIDDEN),
	AUTHENTICATION_EXCEPTION(GeneralErrorMessageResolver.AUTHENTICATION_EXCEPTION,
			"authentication-exception",
			GeneralTitleMessageResolver.SECURITY_ERROR,
			Status.UNAUTHORIZED),
	INVALID_CREDENTIALS(GeneralErrorMessageResolver.INVALID_CREDENTIALS,
			"invalid-credentials",
			GeneralTitleMessageResolver.SECURITY_ERROR,
			Status.UNAUTHORIZED),
	INCORRECT_PASSWORD(GeneralErrorMessageResolver.INCORRECT_PASSWORD,
			"invalid-credentials",
			GeneralTitleMessageResolver.SECURITY_ERROR,
			Status.UNAUTHORIZED),

	NOT_FOUND(GeneralErrorMessageResolver.NOT_FOUND,
			TYPE_NOT_FOUND,
			GeneralTitleMessageResolver.NOT_FOUND,
			Status.BAD_REQUEST),
	FEATURE_NOT_ENABLED(GeneralErrorMessageResolver.FEATURE_NOT_ENABLED,
			TYPE_NOT_FOUND,
			GeneralTitleMessageResolver.INTERNAL_SERVER_ERROR,
			Status.INTERNAL_SERVER_ERROR);
	// @formatter:on

	private final MessageResolver messageResolver;

	private final String typeCode;

	private MessageResolver titleResolver;

	private StatusType status;

	private Object[] params;

	private GeneralErrorResolver(final MessageResolver messageResolver, final String typeCode,
			final MessageResolver titleResolver, final StatusType status) {
		this.messageResolver = messageResolver;
		this.typeCode = typeCode;
		this.titleResolver = titleResolver;
		this.status = status;
	}

	@Override
	public String message() {
		return MessageProvider.getMessage(null, this.messageResolver.defaultMessage(), this.params);
	}

	@Override
	public String localizedMessage() {
		return MessageProvider.getMessage(this.messageResolver, this.params);
	}

	@Override
	public ErrorResolver parameters(final Object... params) {
		this.params = params;
		return this;
	}

	@Override
	public String typeCode() {
		return this.typeCode;
	}

	@Override
	public StatusType status() {
		return this.status != null ? this.status : Status.INTERNAL_SERVER_ERROR;
	}

	@Override
	public String title() {
		return this.titleResolver != null ? MessageProvider.getMessage(this.titleResolver)
				: MessageProvider.getMessage(GeneralTitleMessageResolver.INTERNAL_SERVER_ERROR);
	}

	@Override
	public GeneralErrorResolver status(StatusType status) {
		this.status = status;
		return this;
	}

	@Override
	public ErrorResolver titleResolver(MessageResolver titleResolver) {
		this.titleResolver = titleResolver;
		return this;
	}

}
