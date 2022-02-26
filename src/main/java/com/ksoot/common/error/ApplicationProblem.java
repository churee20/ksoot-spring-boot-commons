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

package com.ksoot.common.error;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.StatusType;
import org.zalando.problem.ThrowableProblem;

import com.ksoot.common.error.resolver.ErrorResolver;

/**
 * Custom, parameterized exception, which can be translated on the client side. For example:
 *
 * <pre>
 * throw new ParameterizedException(&quot;myCustomError&quot;, &quot;hello&quot;, &quot;world&quot;);
 * </pre>
 *
 * Can be translated with:
 *
 * <pre>
 * "error.myCustomError" :  "The server says {{param0}} to {{param1}}"
 * </pre>
 */
public class ApplicationProblem extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;
    
    private ErrorResolver errorResolver;

    private String message;

    private String localizedMessage;
    
    public ApplicationProblem(
            final String title,
            final StatusType status,
            final String detail,
            final URI instance,
            final ThrowableProblem cause,
            final Map<String, Object> parameters) {
    	super(DEFAULT_TYPE, title, status, detail, instance, cause, parameters);
    }
    
    public ApplicationProblem(final String title, final StatusType status,
    		final String detail, final String message,
            final ThrowableProblem cause) {
        this(title, status, detail,
        		null, cause, null);
        this.message = message;
        this.localizedMessage = message;
    }

    public ApplicationProblem(final ErrorResolver resolver) {
        this(resolver.title(), resolver.status(), 
        		resolver.message(), 
        		null, null, null);
        this.errorResolver = resolver;
    }

    public ApplicationProblem(final ErrorResolver resolver, final Throwable cause) {
        this(resolver.title(), resolver.status(), resolver.message(), 
        		null, ProblemUtil.toProblem(cause), null);
        this.errorResolver = resolver;
    }
    
    public ApplicationProblem(final ErrorResolver resolver, final Throwable cause, final Object... args) {
        this(resolver.title(), resolver.status(), resolver.message(), 
        		null, ProblemUtil.toProblem(cause), 
        		problemParameters(args));
        this.errorResolver = resolver;
    }
    
	public ErrorResolver getErrorResolver() {
		return this.errorResolver;
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}

	@Override
	public String getLocalizedMessage() {
		return this.localizedMessage;
	}

	private static Map<String, Object> problemParameters(Object[] args) {
		Map<String, Object> parameters = new LinkedHashMap<>();
		if (ArrayUtils.isNotEmpty(args)) {
			for (int i = 0; i < args.length; i++) {
				parameters.put("arg-" + (i + 1), args[i]);
			}
		}
		return parameters;
	}
}