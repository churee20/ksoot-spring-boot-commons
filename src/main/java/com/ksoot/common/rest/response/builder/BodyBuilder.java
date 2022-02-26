package com.ksoot.common.rest.response.builder;

public interface BodyBuilder<T, R> extends StatusBuilder<T, R> {
	
	public MessageBuilder<T, R> body(T body);
}
