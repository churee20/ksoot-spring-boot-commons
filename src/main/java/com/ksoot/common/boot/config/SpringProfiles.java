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

package com.ksoot.common.boot.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.Environment;

import com.google.common.collect.Sets;

import lombok.NonNull;

/**
 * @author Rajveer Singh
 */
public class SpringProfiles {

	public static final String LOCAL = "local";

	public static final String DEVELOPMENT = "dev";

	public static final String UAT = "uat";

	public static final String LOAD = "load";

	public static final String PRODUCTION = "prod";

	public static final String TEST = "test";

	public static final String INTEGRATION_TEST = "it";

	public static final String SWAGGER = "swagger";

	public static final String NOT_TEST = "!test";

	private static final String SPRING_PROFILE_DEFAULT_KEY = "spring.profiles.default";

	private static final String DEFAULT_PROFILE = LOCAL;

	private static List<String> ACTIVE_PROFILES;

	@NonNull
	private static Environment environment;

	@Autowired
	public SpringProfiles(final Environment environment) {
		SpringProfiles.environment = environment;
	}

	/**
	 * Set a default to use when no profile is configured.
	 * @param app the Spring application
	 */
	public static void addDefaultProfile(final SpringApplication app) {
		Map<String, Object> defaultProfiles = new HashMap<>(1);
		defaultProfiles.put(SPRING_PROFILE_DEFAULT_KEY, DEFAULT_PROFILE);
		app.setDefaultProperties(defaultProfiles);
	}

	public static void addAdditionalProfiles(final SpringApplication app, final String[] additionalProfiles) {
		app.setAdditionalProfiles(additionalProfiles);
	}

	/**
	 * Get the profiles that are applied else get default profiles.
	 * @return profiles
	 */
	public static List<String> getActiveProfiles() {
		if (ACTIVE_PROFILES == null) {
			if (environment.getActiveProfiles().length == 0) {
				ACTIVE_PROFILES = Arrays.asList(environment.getDefaultProfiles());
				return ACTIVE_PROFILES;
			}
			else {
				ACTIVE_PROFILES = Arrays.asList(environment.getActiveProfiles());
				return ACTIVE_PROFILES;
			}
		}
		else {
			return ACTIVE_PROFILES;
		}
	}

	public static boolean isLocal() {
		return getActiveProfiles().contains(LOCAL);
	}

	public static boolean isDev() {
		return getActiveProfiles().contains(DEVELOPMENT);
	}

	public static boolean isIntegrationTest() {
		return getActiveProfiles().contains(INTEGRATION_TEST);
	}

	public static boolean isUat() {
		return getActiveProfiles().contains(UAT);
	}

	public static boolean isLoad() {
		return getActiveProfiles().contains(LOAD);
	}

	public static boolean isProd() {
		return getActiveProfiles().contains(PRODUCTION);
	}

	public static boolean isEnabled(final String profile) {
		return getActiveProfiles().contains(profile);
	}

	public static Set<String> exclusiveProfiles() {
		return Sets.newHashSet(LOCAL, DEVELOPMENT, INTEGRATION_TEST, UAT, LOAD, PRODUCTION);
	}

}
