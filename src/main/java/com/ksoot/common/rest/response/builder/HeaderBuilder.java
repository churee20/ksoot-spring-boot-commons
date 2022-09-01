package com.ksoot.common.rest.response.builder;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

import com.ksoot.common.util.Buildable;

public interface HeaderBuilder<T, R> extends Buildable<R> {
	
    String PREFIX = "X-neosave";

    String INFO = PREFIX + "-Info";

    String WARNING = PREFIX + "-Warning";

    String SUCCESS = PREFIX + "-Success";

    String ERROR = PREFIX + "-Error";

    String ALERT = PREFIX + "-Alert";
    
	public HeaderBuilder<T, R> header(final String name, String... value);
	
	public Buildable<R> headers(final HttpHeaders headers);

	public Buildable<R> headers(final MultiValueMap<String, String> headers);

}
