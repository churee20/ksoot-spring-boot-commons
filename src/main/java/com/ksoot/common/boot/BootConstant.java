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

package com.ksoot.common.boot;

import org.springframework.context.support.AbstractApplicationContext;

/**
 * Application constants.
 */
public interface BootConstant {

	public static interface BeanName {

		String APPLICATION_TASK_EXECUTOR_BEAN_NAME = "applicationTaskExecutor";

		String SCHEDULED_TASK_EXECUTOR_BEAN_NAME = "scheduledTaskExecutor";

		String PROPERTY_SOURCES_PLACEHOLDER_CONFIGURER_BEAN_NAME = "propertySourcesPlaceholderConfigurer";

		String CONFIG_MANAGER_BEAN_NAME = "configManager";

		String PAGINATED_RESOURCE_ASSEMBLER_BEAN_NAME = "pageAssembler";

		String APPLICATION_EVENT_MULTICASTER_BEAN_NAME = AbstractApplicationContext.APPLICATION_EVENT_MULTICASTER_BEAN_NAME;

		String SECURITY_CONFIGURATION_BEAN_NAME = "securityConfiguration";

		String APPLICATION_EXCEPTION_HANDLER_BEAN_NAME = "applicationExceptionHandler";

		String SECURITY_EXCEPTION_HANDLER_BEAN_NAME = "securityExceptionHandler";

		String WEB_EXCEPTION_HANDLER_BEAN_NAME = "webExceptionHandler";

		String SWAGGER_AUTO_CONFIGURATION_BEAN_NAME = "swaggerAutoConfiguration";

		String PROBLEM_AUTO_CONFIGURATION_BEAN_NAME = "problemAutoConfiguration";

		String APPLICATION_API_DOCKET_BEAN_NAME = "defaultApi";

		String ACTUATOR_API_DOCKET_BEAN_NAME = "actuatorApi";

		String WEB_CONFIGURER_BEAN_NAME = "webConfigurer";

		String COUCHBASE_CONFIGURATION_BEAN_NAME = "couchbaseConfiguration";
		
		String AMAZON_DYNAMODB_BEAN_NAME = "amazonDynamoDB";

	}

}
