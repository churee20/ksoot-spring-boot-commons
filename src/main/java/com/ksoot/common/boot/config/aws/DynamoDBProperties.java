package com.ksoot.common.boot.config.aws;

import javax.validation.Valid;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.awspring.cloud.core.config.AwsClientProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@ConditionalOnProperty(prefix = "cloud.aws.dynamodb", name = "repository-package")
@ConfigurationProperties(prefix = "cloud.aws.dynamodb")
@Valid
public class DynamoDBProperties extends AwsClientProperties {
	
	private String repositoryPackage;
}
