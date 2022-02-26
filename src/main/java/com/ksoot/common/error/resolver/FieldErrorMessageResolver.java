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

import lombok.RequiredArgsConstructor;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.FieldError;

/**
 * @author Rajveer Singh
 */
@RequiredArgsConstructor(staticName = "of")
public class FieldErrorMessageResolver implements MessageSourceResolvable {

	private final FieldError fieldError;
	
	@Override
	public String[] getCodes() {
		return this.fieldError.getCodes();
	}
	
	@Override
	public Object[] getArguments() {
		return this.fieldError.getArguments();
	}

	@Override
	public String getDefaultMessage() {
		return this.fieldError.getDefaultMessage();
	}
}
