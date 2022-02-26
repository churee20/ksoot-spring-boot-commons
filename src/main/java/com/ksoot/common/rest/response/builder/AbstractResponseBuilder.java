package com.ksoot.common.rest.response.builder;

import java.util.Arrays;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

import com.ksoot.common.message.MessageProvider;
import com.ksoot.common.message.MessageResolver;
import com.ksoot.common.util.Buildable;

public abstract class AbstractResponseBuilder<T, R> implements BodyBuilder<T, R>, MessageBuilder<T, R> {
	
	protected HttpStatus status;
	
	protected HttpHeaders headers;
	
	protected T body;
	
	protected AbstractResponseBuilder(final HttpStatus status) {
		Assert.notNull(status, "status must not be null");
		this.status = status;
	}

	protected AbstractResponseBuilder(final T body) {
		Assert.notNull(body, "body must not be null");
		this.body = body;
	}

	protected AbstractResponseBuilder(final T body, final HttpStatus status) {
		Assert.notNull(body, "body must not be null");
		Assert.notNull(status, "status must not be null");
		this.body = body;
		this.status = status;
	}

	@Override
	public MessageBuilder<T, R> body(final T body) {
		Assert.notNull(body, "body must not be null");
		this.body = body;
		return this;
	}
	
	@Override
	public MessageBuilder<T, R> status(final HttpStatus status) {
		Assert.notNull(status, "status must not be null");
		this.status = status;
		return this;
	}

	@Override
	public MessageBuilder<T, R> status(int status) {
		this.status = HttpStatus.valueOf(status);
		return this;
	}
	
	@Override
	public HeaderBuilder<T, R> alert(final MessageResolver messageResolver, final Object... params) {
		Assert.notNull(messageResolver, "messageResolver must not be null");
		this.addHeader(ALERT, MessageProvider.getMessage(messageResolver, params));
		return this;
	}

	@Override
	public HeaderBuilder<T, R> alert(final String message) {
		Assert.hasLength(message, "message must not be empty");
		this.addHeader(ALERT, message);
		return this;
	}

	@Override
	public HeaderBuilder<T, R> info(final MessageResolver messageResolver, final Object... params) {
		Assert.notNull(messageResolver, "messageResolver must not be null");
		this.addHeader(INFO, MessageProvider.getMessage(messageResolver, params));
		return this;
	}

	@Override
	public HeaderBuilder<T, R> info(final String message) {
		Assert.hasLength(message, "message must not be empty");
		this.addHeader(INFO, message);
		return this;
	}

	@Override
	public HeaderBuilder<T, R> warning(final MessageResolver messageResolver, final Object... params) {
		Assert.notNull(messageResolver, "messageResolver must not be null");
		this.addHeader(WARNING, MessageProvider.getMessage(messageResolver, params));
		return this;
	}

	@Override
	public HeaderBuilder<T, R> warning(final String message) {
		Assert.hasLength(message, "message must not be empty");
		this.addHeader(WARNING, message);
		return this;
	}

	@Override
	public HeaderBuilder<T, R> success(final MessageResolver messageResolver, final Object... params) {
		Assert.notNull(messageResolver, "messageResolver must not be null");
		this.addHeader(SUCCESS, MessageProvider.getMessage(messageResolver, params));
		return this;
	}

	@Override
	public HeaderBuilder<T, R> success(final String message) {
		Assert.hasLength(message, "message must not be empty");
		this.addHeader(SUCCESS, message);
		return this;
	}

	@Override
	public HeaderBuilder<T, R> error(final MessageResolver messageResolver, final Object... params) {
		Assert.notNull(messageResolver, "messageResolver must not be null");
		this.addHeader(ERROR, MessageProvider.getMessage(messageResolver, params));
		return this;
	}

	@Override
	public HeaderBuilder<T, R> error(final String message) {
		Assert.hasLength(message, "message must not be empty");
		this.addHeader(ERROR, message);
		return this;
	}
	
	protected void addHeader(final String name, final String... values) {
		if(this.headers == null) {
			this.headers = new HttpHeaders();
		}
		this.headers.put(name, Arrays.asList(values));
	}

	protected void addHeaders(final HttpHeaders headers) {
		if(this.headers == null) {
			this.headers = headers;
		} else {
			this.headers.putAll(headers);
		}
	}
	
	@Override
	public HeaderBuilder<T, R> header(final String name, String... values) {
		Assert.hasLength(name, "Header name must not be null or empty!");
		Assert.isTrue(values != null && values.length > 0, "Header values must not be null or empty!");
		this.addHeader(name, values);
		return this;
	}

	@Override
	public Buildable<R> headers(final HttpHeaders headers) {
		Assert.isTrue(headers != null && !headers.isEmpty(), "headers must not be null or empty");
		this.addHeaders(headers);
		return this;
	}

	@Override
	public Buildable<R> headers(final MultiValueMap<String, String> headers) {
		Assert.isTrue(headers != null && !headers.isEmpty(), "headers must not be null or empty");
		this.addHeaders(HttpHeaders.readOnlyHttpHeaders(headers));
		return this;
	}
}
