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

package com.ksoot.common.message;

import java.util.Locale;

import lombok.NonNull;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;

/**
 * @author Rajveer Singh
 */
public class MessageProvider {

	@NonNull
	private static MessageSource messageSource;

	public MessageProvider(MessageSource messageSource) {
		MessageProvider.messageSource = messageSource;
	}

	public static String getMessage(final String messageCode, final String defaultMessage) {
		return messageSource.getMessage(messageCode, null, defaultMessage, Locale.getDefault());
	}

	public static String getMessage(final String messageCode, final String defaultMessage, final Object... params) {
		return messageSource.getMessage(messageCode, params, defaultMessage, Locale.getDefault());
	}

	public static String getMessage(final MessageResolver messageResolver) {
		return messageSource.getMessage(messageResolver.messageCode(), null, messageResolver.defaultMessage(),
				Locale.getDefault());
	}

	public static String getMessage(final MessageResolver messageResolver, final Object... params) {
		return messageSource.getMessage(messageResolver.messageCode(), params, messageResolver.defaultMessage(),
				Locale.getDefault());
	}

	public static String getMessage(final MessageSourceResolvable resolvable) {
		return messageSource.getMessage(resolvable, Locale.getDefault());
	}

}
