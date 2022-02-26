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

import javax.validation.ConstraintViolation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;

/**
 * @author Rajveer Singh
 */
@Getter
//@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ViolationVM implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonInclude(value = Include.NON_NULL)
	private final String objectName;

	private final String field;

	private final String message;

	@JsonInclude(value = Include.NON_NULL)
	private final String messageCode;

	@JsonInclude(value = Include.NON_NULL)
	private final Object rejectedValue;

	private ViolationVM(ProblemProperties properties, String objectName, String field, String message, String messageCode, Object rejectedValue) {
		this.objectName = properties.isDebugInfo() ? objectName : null;
		this.field = field;
		this.message = message;
		this.messageCode = messageCode.replace("{", "").replace("}", "");
		this.rejectedValue = properties.isDebugInfo() ? rejectedValue : null;
	}

	public static ViolationVM of(ProblemProperties properties, ConstraintViolation<?> voilation) {
		return new ViolationVM(properties, voilation.getRootBeanClass().getName(),
				voilation.getPropertyPath().toString(), voilation.getMessage(),
				voilation.getMessageTemplate(), voilation.getInvalidValue());
	}

}
