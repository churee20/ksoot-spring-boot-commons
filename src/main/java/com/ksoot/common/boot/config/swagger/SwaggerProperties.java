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

package com.ksoot.common.boot.config.swagger;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Rajveer Singh
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@ConditionalOnProperty(prefix = "application.swagger", name = "enabled", havingValue = "true")
@ConfigurationProperties(prefix = "application.swagger")
@Valid
public class SwaggerProperties {

	private boolean enabled = false;

	@NotEmpty
	private String title = "Application API";

	@NotEmpty
	private String description = "API documentation";

	private String version = "0.0.1";

	private String termsOfServiceUrl;

	private String contactName;

	private String contactUrl;

	private String contactEmail;

	private String license;

	private String licenseUrl;

	private String host;

	private String[] protocols = {};
	
	private boolean useDefaultResponseMessages = true;

	private DefaultGroup defaultGroup = new DefaultGroup();
	
	@Getter
	@Setter
	@NoArgsConstructor
	@ToString
	@Valid
	public static class DefaultGroup {

		private boolean enabled = false;
		
		private String name = "default";

		private String[] inclusionPattern = { "/**" };

		private String[] exclusionPattern = { "" };
	}
}
