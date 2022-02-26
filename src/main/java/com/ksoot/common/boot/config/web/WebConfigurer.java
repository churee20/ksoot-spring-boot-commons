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

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ksoot.common.CommonConstant;
import com.ksoot.common.boot.config.CommonComponentsAutoConfiguration;
import com.ksoot.common.boot.config.error.ProblemConstants;
import com.ksoot.common.boot.config.pagination.PaginationAutoConfiguration;

import lombok.extern.slf4j.Slf4j;;

/**
 * Configuration of web application with Servlet 3.0 APIs.
 * 
 * @author Rajveer Singh
 */
@Slf4j
@Configuration(value = WEB_CONFIGURER_BEAN_NAME)
@AutoConfigureAfter(value = { PaginationAutoConfiguration.class, CommonComponentsAutoConfiguration.class })
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnMissingBean(name = WEB_CONFIGURER_BEAN_NAME)
public class WebConfigurer
		implements ServletContextInitializer, WebServerFactoryCustomizer<WebServerFactory>, WebMvcConfigurer {

	private final Environment env;

	private final CorsConfiguration corsConfig;

	private List<HandlerMethodArgumentResolver> customArgumentResolvers;
	
	public WebConfigurer(final Environment env, final CorsConfiguration corsConfig,
			final @Nullable List<HandlerMethodArgumentResolver> customArgumentResolvers) {
		this.env = env;
		this.corsConfig = corsConfig;
		this.customArgumentResolvers = customArgumentResolvers;
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		if (this.env.getActiveProfiles().length != 0) {
			log.info("Web application configuration, using profiles: {}", (Object[]) this.env.getActiveProfiles());
		}
		// someInititalization();
		log.info("Web application fully configured");
	}

	// private void someInititalization() {
	// log.info("Some initialization done");
	// }

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

	/**
	 * Customize the Servlet engine: Mime types, the document root, the cache.
	 */
	@Override
	public void customize(WebServerFactory server) {
		setMimeMappings(server);
	}

	private void setMimeMappings(WebServerFactory server) {
		if (server instanceof ConfigurableServletWebServerFactory) {
			MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
			mappings.add("html", MediaType.TEXT_HTML_VALUE + ";charset=" + StandardCharsets.UTF_8.name().toLowerCase());
			mappings.add("json", MediaType.TEXT_HTML_VALUE + ";charset=" + StandardCharsets.UTF_8.name().toLowerCase());
			ConfigurableServletWebServerFactory servletWebServer = (ConfigurableServletWebServerFactory) server;
			servletWebServer.setMimeMappings(mappings);
		}
	}

	@Override
	public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> argumentResolvers) {
		if (!CollectionUtils.isEmpty(this.customArgumentResolvers)) {
			this.customArgumentResolvers.forEach(argumentResolvers::add);
		}
		// Add any custom method argument resolvers
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
//				.allowedOriginPatterns(allowedOriginPatterns)
				.allowedOrigins(allowedOrigins).allowedMethods(allowedMethods).allowedHeaders(allowedHeaders)
				.exposedHeaders(exposedHeaders).allowCredentials(this.corsConfig.getAllowCredentials())
				.maxAge(this.corsConfig.getMaxAge());

		// Add more mappings...
	}


//	@Bean
//	public CorsFilter corsFilter() {
//		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//		if(CollectionUtils.isNotEmpty(this.corsConfig.getAllowedOrigins())) {
//			log.debug("Registering CORS filter");
//			source.registerCorsConfiguration(CommonConstants.API_CONTEXT, this.corsConfig);
//			source.registerCorsConfiguration("/v2/api-docs", this.corsConfig);
//			source.registerCorsConfiguration("/management/**", this.corsConfig);
//		}
//		
//		return new CorsFilter(source);
//	}

}
