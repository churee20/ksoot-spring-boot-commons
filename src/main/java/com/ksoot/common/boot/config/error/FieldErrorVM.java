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

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ksoot.common.error.resolver.FieldErrorMessageResolver;
import com.ksoot.common.message.MessageProvider;

import lombok.Getter;

import org.springframework.validation.FieldError;

/**
 * @author Rajveer Singh
 */
@Getter
public class FieldErrorVM implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@JsonInclude(value = Include.NON_NULL)
	private final String objectName;

	private final String field;

	private final String message;

	@JsonInclude(value = Include.NON_NULL)
	private final Object rejectedValue;

	@JsonInclude(value = Include.NON_NULL)
	private final String messageCode;

	@JsonInclude(value = Include.NON_NULL)
	private final String[] messageCodes;

	private FieldErrorVM(ProblemProperties properties, String objectName, String field, String message,
			Object rejectedValue, String messageCode, String[] messageCodes) {
		this.objectName = properties.isDebugInfo() ? objectName : null;
		this.field = field;
		this.message = message;
		this.rejectedValue = properties.isDebugInfo() ? rejectedValue : null;
		this.messageCode = properties.isDebugInfo() ? messageCode : null;
		this.messageCodes = properties.isDebugInfo() ? messageCodes : null;
	}

	public static FieldErrorVM of(ProblemProperties properties, final FieldError fieldError) {
		return new FieldErrorVM(properties, fieldError.getObjectName(), fieldError.getField(),
				MessageProvider.getMessage(FieldErrorMessageResolver.of(fieldError)),
				fieldError.getRejectedValue(), fieldError.getCode(), fieldError.getCodes());
	}

}
