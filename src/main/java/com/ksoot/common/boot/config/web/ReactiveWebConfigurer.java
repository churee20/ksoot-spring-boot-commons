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

package com.ksoot.common.boot.config.web;

import static com.ksoot.common.boot.BootConstant.BeanName.WEB_CONFIGURER_BEAN_NAME;

import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.http.CacheControl;
import org.springframework.lang.Nullable;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

import com.ksoot.common.CommonConstant;
import com.ksoot.common.boot.config.CommonComponentsAutoConfiguration;
import com.ksoot.common.boot.config.error.ProblemConstants;
import com.ksoot.common.boot.config.pagination.PaginationAutoConfiguration;;

/**
 * @author Rajveer Singh
 */
@Configuration(value = WEB_CONFIGURER_BEAN_NAME)
@AutoConfigureAfter(value = { PaginationAutoConfiguration.class, CommonComponentsAutoConfiguration.class })
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnClass(value = { HandlerResult.class, WebFluxConfigurer.class })
@ConditionalOnMissingBean(name = WEB_CONFIGURER_BEAN_NAME)
@EnableWebFlux
public class ReactiveWebConfigurer implements WebFluxConfigurer {

	private CorsConfiguration corsConfig;

	public ReactiveWebConfigurer(final @Nullable CorsConfiguration corsConfig) {
		this.corsConfig = corsConfig;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/swagger-ui.html**").addResourceLocations("classpath:/META-INF/resources/")
				.setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS));
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/")
				.setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS));
		// "/errors/**" --->> "classpath:/static/problems/"
		registry.addResourceHandler(ProblemConstants.ERROR_DECRIPTION_FILE_URI + "/**")
				.addResourceLocations(ProblemConstants.ERROR_STATIC_RESOURCES_PATH)
				.setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS));
	}

	@Override
	public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
		// Any argument resolver defined as bean is already configured
		// Or Add custom argument resolvers explicitly
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
		registrar.setUseIsoFormat(true);
		registrar.registerFormatters(registry);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {

		String[] allowedOriginPatterns = null;
		if (CollectionUtils.isNotEmpty(this.corsConfig.getAllowedOriginPatterns())) {
			allowedOriginPatterns = new String[this.corsConfig.getAllowedOriginPatterns().size()];
			this.corsConfig.getAllowedOriginPatterns().toArray(allowedOriginPatterns);
		}
		else {
			allowedOriginPatterns = new String[] { CorsConfiguration.ALL };
		}

		String[] allowedOrigins = null;
		if (CollectionUtils.isNotEmpty(this.corsConfig.getAllowedOrigins())) {
			allowedOrigins = new String[this.corsConfig.getAllowedOrigins().size()];
			this.corsConfig.getAllowedOrigins().toArray(allowedOrigins);
		}
		else {
			allowedOrigins = new String[] { CorsConfiguration.ALL };
		}

		String[] allowedMethods = null;
		if (CollectionUtils.isNotEmpty(this.corsConfig.getAllowedMethods())) {
			allowedMethods = new String[this.corsConfig.getAllowedMethods().size()];
			this.corsConfig.getAllowedMethods().toArray(allowedMethods);
		}
		else {
			allowedMethods = new String[] { CorsConfiguration.ALL };
		}

		String[] allowedHeaders = null;
		if (CollectionUtils.isNotEmpty(this.corsConfig.getAllowedHeaders())) {
			allowedHeaders = new String[this.corsConfig.getAllowedHeaders().size()];
			this.corsConfig.getAllowedHeaders().toArray(allowedHeaders);
		}
		else {
			allowedHeaders = new String[] { CorsConfiguration.ALL };
		}

		String[] exposedHeaders = null;
		if (CollectionUtils.isNotEmpty(this.corsConfig.getExposedHeaders())) {
			exposedHeaders = new String[this.corsConfig.getExposedHeaders().size()];
			this.corsConfig.getExposedHeaders().toArray(exposedHeaders);
		}
		else {
			exposedHeaders = new String[] { CorsConfiguration.ALL };
		}

		registry.addMapping(CommonConstant.API_CONTEXT)
				// .allowedOriginPatterns(allowedOriginPatterns)
				.allowedOrigins(allowedOrigins).allowedMethods(allowedMethods).allowedHeaders(allowedHeaders)
				.exposedHeaders(exposedHeaders).allowCredentials(this.corsConfig.getAllowCredentials())
				.maxAge(this.corsConfig.getMaxAge());

		// Add more mappings...
	}

	// @Bean
	// public CorsFilter corsFilter() {
	// UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	// if(CollectionUtils.isNotEmpty(this.corsConfig.getAllowedOrigins())) {
	// log.debug("Registering CORS filter");
	// source.registerCorsConfiguration(CommonConstants.API_CONTEXT, this.corsConfig);
	// source.registerCorsConfiguration("/v2/api-docs", this.corsConfig);
	// source.registerCorsConfiguration("/management/**", this.corsConfig);
	// }
	//
	// return new CorsFilter(source);
	// }

}
