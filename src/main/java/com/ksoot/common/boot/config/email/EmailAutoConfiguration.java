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

package com.ksoot.common.boot.config.email;

import javax.activation.MimeType;
import javax.mail.internet.MimeMessage;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.ksoot.common.boot.config.email.EmailAutoConfiguration.EmailAutoConfigurationCondition;


/**
 * @author Rajveer Singh
 */
@Configuration
@EnableConfigurationProperties({ MailProperties.class, EmailProperties.class })
@ConditionalOnProperty(prefix = "application.email", name = "enabled", havingValue = "true")
@Conditional(EmailAutoConfigurationCondition.class)
@ConditionalOnClass({ MimeMessage.class, MimeType.class, SpringTemplateEngine.class })
@ConditionalOnMissingBean(EmailAutoConfiguration.class)
@AutoConfigureAfter(MailSenderAutoConfiguration.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Import(MailSenderAutoConfiguration.class)
public class EmailAutoConfiguration {

	@Bean
	public EmailClient emailClient(final JavaMailSender mailSender, 
			final SpringTemplateEngine templateEngine,
			final EmailProperties properties) {
		return new EmailClient(mailSender, templateEngine, properties);
	}

	/**
	 * Condition to trigger the creation of a {@link MailSender}. This kicks in if either
	 * the host or jndi name property is set.
	 */
	static class EmailAutoConfigurationCondition extends AnyNestedCondition {

		EmailAutoConfigurationCondition() {
			super(ConfigurationPhase.PARSE_CONFIGURATION);
		}

		@ConditionalOnProperty(prefix = "spring.mail", name = "host")
		static class HostProperty {

		}

		@ConditionalOnProperty(prefix = "spring.mail", name = "jndi-name")
		static class JndiNameProperty {

		}
	}
}
