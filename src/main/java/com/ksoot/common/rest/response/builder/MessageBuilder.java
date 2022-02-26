package com.ksoot.common.rest.response.builder;

import com.ksoot.common.message.MessageResolver;

public interface MessageBuilder<T, R> extends HeaderBuilder<T, R> {
	
	public HeaderBuilder<T, R> alert(final MessageResolver messageResolver, final Object... params);

	public HeaderBuilder<T, R> alert(final String message);
	
    public HeaderBuilder<T, R> info(final MessageResolver messageResolver, final Object... params);

    public HeaderBuilder<T, R> info(final String message);
    
    public HeaderBuilder<T, R> warning(final MessageResolver messageResolver, final Object... params);

    public HeaderBuilder<T, R> warning(final String message);
    
    public HeaderBuilder<T, R> success(final MessageResolver messageResolver, final Object... params);

    public HeaderBuilder<T, R> success(final String message);
    
    public HeaderBuilder<T, R> error(final MessageResolver messageResolver, final Object... params);

    public HeaderBuilder<T, R> error(final String message);
}
