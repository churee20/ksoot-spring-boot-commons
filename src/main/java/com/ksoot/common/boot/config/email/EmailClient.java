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

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.Assert;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

/**
 * @author Rajveer Singh
 */
public class EmailClient {

	private static JavaMailSender mailSender;

	private static SpringTemplateEngine templateEngine;
	
	private static EmailProperties properties;
	
	public EmailClient(final JavaMailSender mailSender, 
			final SpringTemplateEngine templateEngine,
			final EmailProperties properties) {
		EmailClient.mailSender = mailSender;
		EmailClient.templateEngine = templateEngine;
		EmailClient.properties = properties;
	}
	
	public RecipientsBuilder subject(final String subject) {
		return new EmailHelper(subject);
	}
	
	public interface RecipientsBuilder {
		public CcRecipientsBuilder to(final String recipient, final String...others);
	}

	public interface CcRecipientsBuilder extends BccRecipientsBuilder {
		public BccRecipientsBuilder cc(final String... ccRecipients);
	}
	
	public interface BccRecipientsBuilder extends BodyBuilder {
		public BodyBuilder bcc(final String... bccRecipients);
	}
	
	public interface BodyBuilder {
		public ImageBuilder body(final String body, final boolean isHtml);
		public ImageBuilder template(final String name);
	}

	public interface ImageBuilder extends ParametersBuilder {
		public ParametersBuilder inlineImage(final String contentId, final String fileName);
		public ParametersBuilder inlineImage(final String contentId, final Resource image);
		public ParametersBuilder inlineImages(final Map<String, String> images);
	}
	
	public interface ParametersBuilder extends AttachmentBuilder {
		public AttachmentBuilder parameters(final Map<String, Object> data);
	}
	
	public interface AttachmentBuilder extends EmailSupport {
		public EmailSupport attach(final String...fileNames);
		public EmailSupport attach(final Resource...files);
	}
	
	public interface EmailSupport {
		public void send() throws MessagingException;
	}
	
	public static class EmailHelper implements RecipientsBuilder, CcRecipientsBuilder, ImageBuilder,
			ParametersBuilder {

		private String subject;
		
		private String[] recipients;

		private String[] ccRecipients;

		private String[] bccRecipients;
		
		private String body;
		
		private boolean isBodyHtml;
		
		private String template;
		
		private Map<String, Resource> images;

		private Resource[] attachments;
		
		private Map<String, Object> parameters;
		
		EmailHelper(final String subject) {
			this.subject = subject;
		}

		@Override
		public CcRecipientsBuilder to(final String recipient, final String... others) {
	        Assert.hasText(recipient, "recipient must not be null or empty!");
	        this.recipients = ArrayUtils.isNotEmpty(others) 
	        		? ArrayUtils.addFirst(others, recipient) 
	        				: ArrayUtils.toArray(recipient);
	        return this;
		}

		@Override
		public BccRecipientsBuilder cc(final String... ccRecipients) {
	        this.ccRecipients = ccRecipients;
	        return this;
		}

		@Override
		public BodyBuilder bcc(final String... bccRecipients) {
	        this.bccRecipients = bccRecipients;
	        return this;
		}

		@Override
		public ImageBuilder body(final String body, final boolean isHtml) {
	        Assert.hasText(body, "body must not be null or empty!");
	        this.body = body;
	        return this;
		}

		@Override
		public ImageBuilder template(final String fileName) {
	        Assert.hasText(fileName, "template fileName must not be null or empty!");
	        this.template = fileName;
	        return this;
		}

		@Override
		public ParametersBuilder inlineImage(final String contentId, final String fileName) {
	        Assert.hasText(contentId, "Inline image contentId must not be null or empty!");
	        Assert.hasText(fileName, "Inline image fileName must not be null or empty!");
	        Resource imageFile = new ClassPathResource(fileName);
	        Assert.isTrue(imageFile.exists(), "Inline image file: " + fileName + " does not exist!");
	        this.images = Collections.singletonMap(contentId, imageFile);
			return this;
		}

		public ParametersBuilder inlineImage(final String contentId, final Resource image) {
	        Assert.hasText(contentId, "Inline image contentId must not be null or empty!");
	        Assert.notNull(image, "Inline image must not be null!");
	        Assert.isTrue(image.exists(), "Inline image file: " + image + " does not exist!");
	        this.images = Collections.singletonMap(contentId, image);
			return this;
		}
		
		@Override
		public ParametersBuilder inlineImages(final Map<String, String> images) {
	        if(MapUtils.isNotEmpty(images)) {
	        	this.images = images.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                    e -> {
                    	Resource imageFile = new ClassPathResource(e.getValue());
            	        Assert.isTrue(imageFile.exists(), "Image file: " + e.getValue() + " does not exist!");
            	        return imageFile;
                    }));
	        }
			return this;
		}

		@Override
		public AttachmentBuilder parameters(final Map<String, Object> data) {
	        this.parameters = data;
	        return this;
		}

		@Override
		public EmailSupport attach(final String...fileNames) {
			if(ArrayUtils.isNotEmpty(fileNames)) {
		        this.attachments = Arrays.stream(fileNames).map(fileName -> {
		        	Resource imageFile = new ClassPathResource(fileName);
	    	        Assert.isTrue(imageFile.exists(), "Attachment file: " + fileName+ " does not exist!");
	    	        return imageFile;
		        }).toArray(size -> new Resource[fileNames.length]);
			}
	        return this;
		}

		@Override
		public EmailSupport attach(final Resource...files) {
			if(ArrayUtils.isNotEmpty(files)) {
				Assert.isTrue(ArrayUtils.isNotEmpty(files), "Attachment files must not be null or empty!");
				this.attachments = files;
			}
	        return this;
		}
		
		@Override
		public void send() throws MessagingException {
			// Prepare the evaluation context
	        final Context context = new Context();
	        if(MapUtils.isNotEmpty(this.parameters)) {
	        	context.setVariables(this.parameters);
	        }

	        StandardCharsets.UTF_8.name();
	        // Prepare message using a Spring helper
	        final MimeMessage mimeMessage = mailSender.createMimeMessage();
	        final MimeMessageHelper message = MapUtils.isNotEmpty(this.images) || ArrayUtils.isNotEmpty(this.attachments)
	        		? new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name())
	        				: new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
	        message.setSubject(this.subject);
//	        message.setFrom("developer.portal@witty.tech"); // Never do this, From can not be different than spring.mail.usernam
	        message.setTo(this.recipients);
	        
	        if(ArrayUtils.isNotEmpty(this.ccRecipients)) {
	        	message.setCc(this.ccRecipients);
	        }
	        if(ArrayUtils.isNotEmpty(this.bccRecipients)) {
	        	message.setBcc(this.bccRecipients);
	        }
	        
			if(StringUtils.isNotEmpty(this.body)) {
				message.setText(this.body, this.isBodyHtml);
			} else if(StringUtils.isNotEmpty(this.template)) {
				String content = templateEngine.process(this.template, context);
		        message.setText(content, true);
			}

	        if(MapUtils.isNotEmpty(this.images)) {
				for(Map.Entry<String, Resource> image : this.images.entrySet()) {
					message.addInline(image.getKey(), image.getValue());
				}
	        }
	        
	        if(ArrayUtils.isNotEmpty(this.attachments)) {
	        	for(Resource attachment : this.attachments) {
	        		message.addAttachment(attachment.getFilename(), attachment);
	        	}
	        }
	        
	        if(StringUtils.isNotEmpty(properties.getReplyTo())) {
	        	message.setReplyTo(properties.getReplyTo());
	        }
	        
	        if(properties.isValidateAddresses()) {
	        	message.setValidateAddresses(properties.isValidateAddresses());
	        }
 	        
	        mailSender.send(mimeMessage);
		}
	}
}
