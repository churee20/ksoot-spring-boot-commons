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

package com.ksoot.common.boot.config.swagger.customizer;

import springfox.documentation.spring.web.plugins.Docket;

/**
 * Callback interface that can be implemented by beans wishing to further customize the
 * {@link Docket} in Springfox.
 *
 * @author Rajveer Singh
 */
@FunctionalInterface
public interface SwaggerCustomizer {

	public enum Order {

		ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5);

		private final int value;

		private Order(int value) {
			this.value = value;
		}

		public int value() {
			return this.value;
		}

	}

	/**
	 * Customize the Springfox Docket.
	 * @param docket the Docket to customize
	 */
	void customize(Docket docket);

}
