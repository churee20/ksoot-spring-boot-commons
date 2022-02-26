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

import static com.ksoot.common.boot.BootConstant.BeanName.ACTUATOR_API_DOCKET_BEAN_NAME;
import static com.ksoot.common.boot.BootConstant.BeanName.APPLICATION_API_DOCKET_BEAN_NAME;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;

import com.fasterxml.classmate.TypeResolver;
import com.ksoot.common.boot.config.pagination.PaginationProperties;
import com.ksoot.common.boot.config.security.SecurityProperties;
import com.ksoot.common.boot.config.swagger.customizer.PageableParameterBuilderPlugin;
import com.ksoot.common.boot.config.swagger.customizer.SwaggerApiKeySecurityCustomizer;
import com.ksoot.common.boot.config.swagger.customizer.SwaggerBasicCustomizer;
import com.ksoot.common.boot.config.swagger.customizer.SwaggerCustomizer;
import com.ksoot.common.boot.config.swagger.customizer.SwaggerPaginationCustomizer;

import springfox.boot.starter.autoconfigure.OpenApiAutoConfiguration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


/**
 * Springfox Swagger configuration.
 * <p>
 * Warning! When having a lot of REST endpoints, Springfox can become a performance issue.
 * In that case, you can skip swagger by not including swagger Spring profile in dev/prod
 * application yml, so that this bean is ignored.
 *
 * @author Rajveer Singh
 */
@Configuration
@EnableConfigurationProperties(value = { SwaggerProperties.class, SecurityProperties.class })
@AutoConfigureBefore(OpenApiAutoConfiguration.class)
@ConditionalOnProperty(prefix = "application.swagger", name = "enabled", havingValue = "true")
public class SwaggerConfiguration {
	
	public static Docket newDocket(final String groupName, final Predicate<String> pathSelectors,
			final List<SwaggerCustomizer> swaggerCustomizers) {
		Docket docket = new Docket(DocumentationType.SWAGGER_2).select().paths(pathSelectors)
				// .paths(PathSelectors.any())
				.build().groupName(groupName);
        // Apply all SwaggerCustomizers orderly.
        swaggerCustomizers.forEach(customizer -> customizer.customize(docket));
		return docket;
	}

	@Bean
	@ConditionalOnMissingBean(SwaggerBasicCustomizer.class)
	public SwaggerBasicCustomizer swaggerBasicCustomizer(final SwaggerProperties swaggerProperties, 
			final TypeResolver typeResolver, final ObjectProvider<AlternateTypeRule[]> alternateTypeRules) {
		return new SwaggerBasicCustomizer(swaggerProperties, typeResolver, alternateTypeRules);
	}

	@Configuration
	@EnableConfigurationProperties(value = { SecurityProperties.class })
	@ConditionalOnClass(WebSecurityConfiguration.class)
	@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
	@ConditionalOnProperty(prefix = "application.security", name = "enabled", havingValue = "true")
	@ConditionalOnMissingBean(SwaggerApiKeySecurityCustomizer.class)
	public static class ApiSecurityAutoConfiguration {

		@Bean
		public SwaggerApiKeySecurityCustomizer swaggerSecurityCustomizer(
				final SwaggerProperties swaggerProperties,
				final SecurityProperties securityProperties) {
			return new SwaggerApiKeySecurityCustomizer(swaggerProperties, securityProperties);
		}

	}

	@Configuration
	@EnableConfigurationProperties(PaginationProperties.class)
	@ConditionalOnClass(Pageable.class)
	@ConditionalOnProperty(prefix = "application.pagination", name = "enabled", havingValue = "true")
	@ConditionalOnMissingBean(SwaggerPaginationCustomizer.class)
	public static class ApiPaginationAutoConfiguration {

		@ConditionalOnMissingBean(PageableParameterBuilderPlugin.class)
		@Bean
		public PageableParameterBuilderPlugin pageableParameterBuilderPlugin(final PaginationProperties properties,
				final TypeNameExtractor typeNameExtractor, final TypeResolver typeResolver) {
			return new PageableParameterBuilderPlugin(properties, typeNameExtractor, typeResolver);
		}

		@Bean
		public SwaggerPaginationCustomizer swaggerPaginationCustomizer() {
			return new SwaggerPaginationCustomizer();
		}

	}

	@Configuration
	@EnableConfigurationProperties(SwaggerProperties.class)
	@ConditionalOnProperty(prefix = "application.swagger.default-group", name = "enabled", havingValue = "true")
	@ConditionalOnMissingBean(name = APPLICATION_API_DOCKET_BEAN_NAME)
	public static class DefaultGroupAutoConfiguration {

		@ConditionalOnMissingBean(name = APPLICATION_API_DOCKET_BEAN_NAME)
		@Bean(APPLICATION_API_DOCKET_BEAN_NAME)
		public Docket defaultApi(final SwaggerProperties properties,
				final List<SwaggerCustomizer> swaggerCustomizers) {
			
			Predicate<String> inclusion = Arrays.stream(properties.getDefaultGroup().getInclusionPattern())
	    			.map(uri -> PathSelectors.ant(uri))
	    			.reduce(Predicate::or).orElse(x -> true);
			Predicate<String> exclusion = Arrays.stream(properties.getDefaultGroup().getExclusionPattern())
	    			.map(uri -> PathSelectors.ant(uri).negate())
	    			.reduce(Predicate::and).orElse(x -> true);

			return SwaggerConfiguration.newDocket(properties.getDefaultGroup().getName(), 
					inclusion.and(exclusion),
					swaggerCustomizers);
		}
	}
	
	@Configuration
	@ConditionalOnClass(name = "org.springframework.boot.actuate.web.mappings.MappingsEndpoint")
	@ConditionalOnMissingBean(name = ACTUATOR_API_DOCKET_BEAN_NAME)
	public static class ActuatorGroupAutoConfiguration {

		private static final String ACTUATOR_API_GROUP_NAME = "actuator";

		@Bean(ACTUATOR_API_DOCKET_BEAN_NAME)
		public Docket actuatorApi(final List<SwaggerCustomizer> swaggerCustomizers) {
			return SwaggerConfiguration.newDocket(ACTUATOR_API_GROUP_NAME, PathSelectors.regex("/actuator.*"),
					swaggerCustomizers);
		}

	}
	
}
