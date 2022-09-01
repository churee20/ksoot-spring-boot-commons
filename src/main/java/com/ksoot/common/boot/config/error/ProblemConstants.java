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

package com.ksoot.common.boot.config.error;

/**
 * @author Rajveer Singh
 */
public interface ProblemConstants {

	String ERROR_DECRIPTION_FILE_URI = "/errors";

	String ERROR_STATIC_RESOURCES_PATH = "classpath:/static/problems/";

	String ERROR_DECRIPTION_FILE_NAME = "problem.html";

	String ERROR_DECRIPTION_FILE = ERROR_DECRIPTION_FILE_URI + "/" + ERROR_DECRIPTION_FILE_NAME;
	
	String MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION_PREFIX = "missing.";
	
	String DATA_INTEGRITY_VIOLATION_EXCEPTION_PREFIX = "data.integrity.violation.";

	public static interface Keys {

		String MESSAGE = "message";

		String LOCALIZED_MESSAGE = "localizedMessage";

		String HTTP_METHOD = "method";

		String TIMESTAMP = "timestamp";

		String VIOLATIONS = "violations";

		String FIELD_ERRORS = "fieldErrors";

		String STACKTRACE = "stacktrace";

	}

}
