package com.ksoot.common.boot.config.error;

import static com.ksoot.common.boot.config.error.ProblemConstants.ERROR_DECRIPTION_FILE;
import static com.ksoot.common.boot.config.error.ProblemConstants.Keys.FIELD_ERRORS;
import static com.ksoot.common.boot.config.error.ProblemConstants.Keys.LOCALIZED_MESSAGE;
import static com.ksoot.common.boot.config.error.ProblemConstants.Keys.MESSAGE;
import static com.ksoot.common.boot.config.error.ProblemConstants.Keys.TIMESTAMP;
import static com.ksoot.common.boot.config.error.ProblemConstants.Keys.VIOLATIONS;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.List;

import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.StatusType;

import com.ksoot.common.error.resolver.ErrorResolver;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultErrorBuilder implements ErrorBuilder {

	private final String contextPath;

	@Override
	public ProblemBuilder problemBuilder(final ErrorResolver errorResolver, final URI requestUri) {
		return Problem.builder().withTitle(errorResolver.title()).withInstance(requestUri)
				.withType(generateType(requestUri, errorResolver.typeCode())).withStatus(errorResolver.status())
				.with(MESSAGE, errorResolver.message()).with(LOCALIZED_MESSAGE, errorResolver.message())
				.with(TIMESTAMP, time());
	}

	@Override
	public ProblemBuilder problemBuilder(final String title, final URI requestUri, final URI type,
			final StatusType status, final String message, final String localizedMessage) {
		return Problem.builder().withTitle(title).withInstance(requestUri).withType(type)
				.withStatus(status).with(MESSAGE, message).with(LOCALIZED_MESSAGE, localizedMessage)
				.with(TIMESTAMP, time());
	}

	@Override
	public URI generateType(final URI requestUri, final String typeCode) {
		URI type = null;
		try {
			type = new URI(requestUri.getScheme(), requestUri.getAuthority(),
					this.contextPath + ERROR_DECRIPTION_FILE + "#" + typeCode, null, null);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return type;
	}

	@Override
	public ProblemBuilder addFieldErrors(final ProblemBuilder problemBuilder, final List<FieldErrorVM> fieldErrors) {
		return problemBuilder.with(FIELD_ERRORS, fieldErrors);
	}

	@Override
	public ProblemBuilder addViolations(final ProblemBuilder problemBuilder, final List<ViolationVM> violations) {
		return problemBuilder.with(VIOLATIONS, violations);
	}

	public OffsetDateTime time() {
		// return OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS);
		return OffsetDateTime.now();
	}

}
