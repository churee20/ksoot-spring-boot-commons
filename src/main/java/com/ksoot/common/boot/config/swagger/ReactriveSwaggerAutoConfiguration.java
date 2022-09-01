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

import static com.ksoot.common.boot.BootConstant.BeanName.SWAGGER_AUTO_CONFIGURATION_BEAN_NAME;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.ksoot.common.boot.config.security.SecurityProperties;

import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.boot.starter.autoconfigure.OpenApiAutoConfiguration;
import springfox.boot.starter.autoconfigure.SwaggerUiWebFluxConfiguration;
import springfox.documentation.spring.web.SpringfoxWebFluxConfiguration;
import springfox.documentation.swagger2.configuration.Swagger2DocumentationConfiguration;

/**
 * Springfox Swagger configuration.
 * <p>
 * Warning! When having a lot of REST endpoints, Springfox can become a performance issue.
 * In that case, you can skip swagger by not including swagger Spring profile in dev/prod
 * application yml, so that this bean is ignored.
 *
 * @author Rajveer Singh
 */
@Configuration(value = SWAGGER_AUTO_CONFIGURATION_BEAN_NAME)
@ConditionalOnClass(SpringfoxWebFluxConfiguration.class)
@EnableConfigurationProperties(value = { SwaggerProperties.class, SecurityProperties.class })
@AutoConfigureBefore(OpenApiAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnProperty(prefix = "application.swagger", name = "enabled", havingValue = "true")
@ConditionalOnMissingBean(name = SWAGGER_AUTO_CONFIGURATION_BEAN_NAME)
@Import({ BeanValidatorPluginsConfiguration.class, Swagger2DocumentationConfiguration.class,
		SwaggerUiWebFluxConfiguration.class, SwaggerConfiguration.class })
public class ReactriveSwaggerAutoConfiguration {

}
