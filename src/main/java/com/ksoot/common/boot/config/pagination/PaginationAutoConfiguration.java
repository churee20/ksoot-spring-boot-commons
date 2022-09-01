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

package com.ksoot.common.boot.config.pagination;

import static com.ksoot.common.boot.BootConstant.BeanName.PAGINATED_RESOURCE_ASSEMBLER_BEAN_NAME;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.HateoasSortHandlerMethodArgumentResolver;
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver;
import org.springframework.data.web.ReactiveSortHandlerMethodArgumentResolver;
import org.springframework.lang.Nullable;

import com.ksoot.common.boot.config.swagger.SwaggerAutoConfiguration;

/**
 * @author Rajveer Singh
 */
@Configuration
@EnableConfigurationProperties(PaginationProperties.class)
@ConditionalOnClass(Pageable.class)
@ConditionalOnProperty(prefix = "application.pagination", name = "enabled", havingValue = "true")
@ConditionalOnMissingBean(PaginationAutoConfiguration.class)
public class PaginationAutoConfiguration {

	@Configuration
	@ConditionalOnClass(Pageable.class)
	@EnableConfigurationProperties(PaginationProperties.class)
	@AutoConfigureAfter(SwaggerAutoConfiguration.class)
	public static class PageAssemblerConfiguration {

		private final PaginationProperties properties;

		public PageAssemblerConfiguration(PaginationProperties properties) {
			this.properties = properties;
		}

		@ConditionalOnMissingBean(name = PAGINATED_RESOURCE_ASSEMBLER_BEAN_NAME)
		@Bean(PAGINATED_RESOURCE_ASSEMBLER_BEAN_NAME)
		public PaginatedResourceAssembler pageAssembler(
				@Nullable HateoasPageableHandlerMethodArgumentResolver resolver) {
			return new PaginatedResourceAssembler(resolver, this.properties.isFirstAndLastRels());
		}

		@ConditionalOnClass(HateoasPageableHandlerMethodArgumentResolver.class)
		@ConditionalOnMissingBean(HateoasPageableHandlerMethodArgumentResolver.class)
		@Bean
		public HateoasPageableHandlerMethodArgumentResolver hateoasPageableHandlerMethodArgumentResolver() {
			HateoasSortHandlerMethodArgumentResolver DEFAULT_SORT_RESOLVER = new HateoasSortHandlerMethodArgumentResolver();
			DEFAULT_SORT_RESOLVER.setSortParameter(this.properties.getSortParamName());
			HateoasPageableHandlerMethodArgumentResolver resolver = new HateoasPageableHandlerMethodArgumentResolver(
					DEFAULT_SORT_RESOLVER);
			resolver.setPageParameterName(this.properties.getPageParamName());
			resolver.setSizeParameterName(this.properties.getSizeParamName());
			resolver.setMaxPageSize(this.properties.getMaxPageSize());
			resolver.setFallbackPageable(this.properties.defaultPage());
			return resolver;
		}
	}

	@Configuration
	@EnableConfigurationProperties(PaginationProperties.class)
	@ConditionalOnClass(Pageable.class)
	@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
	@AutoConfigureAfter(SwaggerAutoConfiguration.class)
	@ConditionalOnProperty(prefix = "application.pagination", name = "enabled", havingValue = "true")
	public static class ReactivePageableHandlerMethodArgumentResolverAutoConfiguration {

		private final PaginationProperties properties;

		public ReactivePageableHandlerMethodArgumentResolverAutoConfiguration(PaginationProperties properties) {
			this.properties = properties;
		}

		@ConditionalOnClass(ReactivePageableHandlerMethodArgumentResolver.class)
		@ConditionalOnMissingBean(ReactivePageableHandlerMethodArgumentResolver.class)
		@Bean
		public ReactivePageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver() {
			ReactiveSortHandlerMethodArgumentResolver DEFAULT_SORT_RESOLVER = new ReactiveSortHandlerMethodArgumentResolver();
			DEFAULT_SORT_RESOLVER.setSortParameter(this.properties.getSortParamName());
			ReactivePageableHandlerMethodArgumentResolver resolver = new ReactivePageableHandlerMethodArgumentResolver(
					DEFAULT_SORT_RESOLVER);
			resolver.setPageParameterName(this.properties.getPageParamName());
			resolver.setSizeParameterName(this.properties.getSizeParamName());
			resolver.setMaxPageSize(this.properties.getMaxPageSize());
			resolver.setFallbackPageable(this.properties.defaultPage());
			return resolver;
		}
	}
}
