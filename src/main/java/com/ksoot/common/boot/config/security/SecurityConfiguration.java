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

package com.ksoot.common.boot.config.security;

import static com.ksoot.common.boot.BootConstant.BeanName.SECURITY_CONFIGURATION_BEAN_NAME;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

/**
 * @author Rajveer Singh
 */
@Configuration(value = SECURITY_CONFIGURATION_BEAN_NAME)
@EnableConfigurationProperties(SecurityProperties.class)
@ConditionalOnProperty(prefix = "application.security", name = "enabled", havingValue = "true")
@ConditionalOnClass(WebSecurityConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnMissingBean(name = SECURITY_CONFIGURATION_BEAN_NAME)
@AutoConfigureBefore(SecurityAutoConfiguration.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Import(SecurityProblemSupport.class)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // Allow method annotations like @PreAuthorize
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final SecurityProperties properties;

    private final SecurityProblemSupport problemSupport;

    public SecurityConfiguration(SecurityProperties properties, SecurityProblemSupport problemSupport) {
        this.properties = properties;
        this.problemSupport = problemSupport;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	// @formatter:off
        http.csrf().disable()
                .authorizeRequests()
                	.antMatchers("/swagger-resources/**", "/swagger-ui/**", "/v2/api-docs", "/webjars/**").permitAll()
                    .antMatchers(this.properties.getUnsecuredUris()).permitAll()
                    .anyRequest().authenticated();
        http.exceptionHandling().authenticationEntryPoint(this.problemSupport)
        		.accessDeniedHandler(this.problemSupport);
        // @formatter:on
    }
}
