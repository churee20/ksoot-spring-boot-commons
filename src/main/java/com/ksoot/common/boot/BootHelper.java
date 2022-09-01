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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.core.env.Environment;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.ksoot.common.CommonConstant;
import com.ksoot.common.boot.config.SpringProfiles;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Rajveer Singh
 */
@Slf4j
public class BootHelper {

    public static SpringApplication boot(SpringApplication application, final String[] args) {
        SpringProfiles.addDefaultProfile(application);
        Environment env = application.run(args).getEnvironment();
    	validateProfiles(env);
        logApplicationStartup(application.getWebApplicationType(), env);
		TimeZone.setDefault(CommonConstant.DEFAULT_TIME_ZONE);
		log.info("Set Default time zone: " + CommonConstant.DEFAULT_TIME_ZONE.getID());
        return application;
    }
    
	private static void validateProfiles(Environment env) {
        Set<String> activeProfiles = Sets.newHashSet(env.getActiveProfiles());
        Set<String> defaultProfiles = Sets.newHashSet(env.getDefaultProfiles());
        Set<String> exclusiveProfiles = SpringProfiles.exclusiveProfiles();
        SetView<String> intersection = Sets.intersection(exclusiveProfiles, activeProfiles.isEmpty() 
        		? defaultProfiles: activeProfiles);

        if (intersection.size() == 0) {
            log.error("You have misconfigured your application! It should run "
                    + "with exactly one of active profiles: " + exclusiveProfiles + ", depending on environment.");
        }
        else if (intersection.size() > 1) {
            log.error("You have misconfigured your application! It should not run with active profiles " + intersection
                    + " togeather. Exactly one of profiles: " + exclusiveProfiles
                    + " must be active, depending on environment.");
        }
    }

	private static void logApplicationStartup(final WebApplicationType webApplicationType, Environment env) {
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        String serverPort = env.getProperty("server.port") == null ? "" + 8080 : env.getProperty("server.port");
        String contextPath = webApplicationType == WebApplicationType.REACTIVE 
        		? env.getProperty("spring.webflux.base-path")
        		: env.getProperty("server.servlet.context-path");
        
        if (StringUtils.isBlank(contextPath)) {
            contextPath = "";
        } else {
        	contextPath = "/" + contextPath;
        }
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        String activeProfile = env.getProperty("spring.profiles.active");
        String features = Arrays.stream(env.getActiveProfiles()).filter(feature -> !feature.equals(activeProfile))
                .collect(Collectors.joining(", "));
        String environment = StringUtils.isEmpty(activeProfile) ? Arrays.stream(env.getDefaultProfiles()).collect(Collectors.joining(", ")) : activeProfile;
        
        String applicationInfo = String.format("Application '%1$s' of type: '%2$s' is running! \n\t", 
        		env.getProperty("spring.application.name"), webApplicationType);
        String localUrl = String.format("Access Local: \t\t%1$s://localhost:%2$s%3$s\n\t", protocol, serverPort, 
                	contextPath);
        String swaggerUrl = String.format(" \t%1$s://localhost:%2$s%3$s/swagger-ui/index.html\n\t", 
        				protocol, serverPort, contextPath);
        String externalUrl = String.format("Access External: \t%1$s://%2$s:%3$s%4$s\n\t", 
        		protocol, hostAddress, serverPort, contextPath);
        String deploymentEnv = String.format("Environment: \t\t%1$s\n\t", environment);
        String activeFeatures = String.format("Features: \t\t%1$s\n\t", features);

        boolean swagger = env.getProperty("application.swagger.enabled", Boolean.class, false);
        boolean pagination = env.getProperty("application.pagination.enabled", Boolean.class, false);
        boolean problemHandling = env.getProperty("application.problem.enabled", Boolean.class, false);
        boolean security = env.getProperty("application.security.enabled", Boolean.class, false);
        boolean email = env.getProperty("application.email.enabled", Boolean.class, false)
        		&& (env.containsProperty("spring.mail.host") || env.containsProperty("spring.mail.jndi-name"));
		
        String configurations = "cors" + (security ? ", security" : "") 
        		+ (swagger ? ", swagger" : "")
        		+ (pagination ? ", pagination" : "")
        		+ (problemHandling ? ", problem-handling" : "")
        		+ (email ? ", email" : "");
        String enabledConfigurations = String.format("Enabled Configurations: %1$s", configurations);
        
        String logInfo = applicationInfo + localUrl + 
        		(SpringProfiles.isEnabled(SpringProfiles.SWAGGER) 
        				? "Swagger customized:" + swaggerUrl
        				: "Swagger default:" + swaggerUrl) 
        		+ externalUrl + deploymentEnv + activeFeatures + enabledConfigurations;
        log.info(
                "\n-------------------------------------------------------------------------------------------\n\t"
                        + logInfo
                        + "\n-------------------------------------------------------------------------------------------");
    }
}
