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

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ksoot.common.CommonConstant;

import lombok.Value;

/**
 * @author Rajveer Singh
 */
@Value
public class IdentityHelper {

    public enum ATTRIBUTE_NAME {

        //@formatter:off
	    PREFERRED_USERNAME("preferred_username");
		//@formatter:on

        private final String value;

        private ATTRIBUTE_NAME(final String value) {
            this.value = value;
        }

        public String value() {
            return this.value;
        }
    }

    private IdentityHelper() {
        // This class is not supposed to be instantiated
    }
   
    public static String getLoginName() {
    	SecurityContext securityContext = SecurityContextHolder.getContext();
	    Authentication authentication = securityContext.getAuthentication();
	    if (authentication != null) {
	    	return authentication.getName();
	    }
	    else {
	    	throw new InsufficientAuthenticationException("Authentication required");
	    }
    }

    public static String getAuditUserId() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        else {
            return CommonConstant.SYSTEM_USER;
        }
    }
    
    public static Principal getPrinciple() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            return (Principal) authentication.getPrincipal();
        }
        else {
            throw new InsufficientAuthenticationException("Authentication required");
        }
    }

    public static List<String> getRoles() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            return authentication.getAuthorities().stream()
            		.map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        }
        else {
            throw new InsufficientAuthenticationException("Authentication required");
        }
    }

}
