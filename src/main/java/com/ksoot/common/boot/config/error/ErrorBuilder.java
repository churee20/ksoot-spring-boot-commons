package com.ksoot.common.boot.config.error;

import java.net.URI;
import java.util.List;

import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.StatusType;

import com.ksoot.common.error.resolver.ErrorResolver;

public interface ErrorBuilder {

	ProblemBuilder problemBuilder(final ErrorResolver errorResolver, final URI requestUri);

	ProblemBuilder problemBuilder(final String title, final URI requestUri, final URI type,
			final StatusType status, final String message, final String localizedMessage);
	
	URI generateType(final URI requestUri, final String typeCode);

	ProblemBuilder addFieldErrors(final ProblemBuilder problemBuilder, final List<FieldErrorVM> fieldErrors);

	ProblemBuilder addViolations(final ProblemBuilder problemBuilder, final List<ViolationVM> violations);

}
