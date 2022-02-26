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

import org.zalando.problem.Exceptional;
import org.zalando.problem.ThrowableProblem;

import com.ksoot.common.error.resolver.ErrorResolver;

/**
 * Checked type Problem if needed
 * @author Rajveer Singh
 */
public class ApplicationException extends Exception implements Exceptional {

    private static final long serialVersionUID = 1L;
    
    private Exception cause;
    
    private ErrorResolver errorResolver;
    
    private ApplicationException(
            final String message,
            final Exception cause) {
    	super(message, cause);
    }
    
    private ApplicationException(
            final String message) {
    	super(message);
    }
    
    public ApplicationException(final ErrorResolver resolver) {
        this(resolver.message());
        this.errorResolver = resolver;
    }

    public ApplicationException(final ErrorResolver resolver, final Exception cause) {
        this(resolver.message(), cause);
        this.errorResolver = resolver;
        this.cause = cause;
    }
    
	public ErrorResolver getErrorResolver() {
		return this.errorResolver;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.zalando.problem.Exceptional#getCause()
	 */
	@Override
	public ThrowableProblem getCause() {
		return this.cause == null ? null : ProblemUtil.toProblem(this.cause, this.errorResolver.status());
	}

}
