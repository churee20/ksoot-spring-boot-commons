package com.ksoot.common.rest.response.builder;

import org.springframework.http.HttpStatus;

public interface StatusBuilder<T, R> {
	
	public MessageBuilder<T, R> status(final HttpStatus status);

	public MessageBuilder<T, R> status(final int status);
}
