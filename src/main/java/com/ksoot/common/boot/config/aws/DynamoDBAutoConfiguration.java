package com.ksoot.common.boot.config.aws;

import static com.ksoot.common.boot.BootConstant.BeanName.AMAZON_DYNAMODB_BEAN_NAME;

import org.apache.commons.lang3.StringUtils;
import org.socialsignin.spring.data.dynamodb.config.AbstractDynamoDBConfiguration;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import io.awspring.cloud.core.region.RegionProvider;
import io.awspring.cloud.core.region.StaticRegionProvider;

@Configuration
@ConditionalOnProperty(prefix = "cloud.aws.dynamodb", name = "endpoint")
@ConditionalOnClass(AbstractDynamoDBConfiguration.class)
@EnableConfigurationProperties(DynamoDBProperties.class)
@EnableDynamoDBRepositories(basePackages = "${cloud.aws.dynamodb.repository-package}")
public class DynamoDBAutoConfiguration extends AbstractDynamoDBConfiguration {

	private final AWSCredentialsProvider awsCredentialsProvider;

	private final RegionProvider regionProvider;

	private final DynamoDBProperties properties;

	public DynamoDBAutoConfiguration(final AWSCredentialsProvider awsCredentialsProvider,
			final ObjectProvider<RegionProvider> regionProvider, 
			final DynamoDBProperties properties) {
		this.awsCredentialsProvider = awsCredentialsProvider;
		this.regionProvider = StringUtils.isEmpty(properties.getRegion()) ? regionProvider.getIfAvailable()
				: new StaticRegionProvider(properties.getRegion());
		this.properties = properties;
	}

	@Override
	@ConditionalOnMissingBean(name = AMAZON_DYNAMODB_BEAN_NAME)
	@Bean
	public AmazonDynamoDB amazonDynamoDB() {
		AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClientBuilder.standard();
		EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(this.properties.getEndpoint().toString(), 
				regionProvider.getRegion().getName());
		builder.setEndpointConfiguration(endpointConfiguration);
		builder.withCredentials(this.awsCredentialsProvider);
		return builder.build();
	}

	@Override
	public AWSCredentials amazonAWSCredentials() {
		return this.awsCredentialsProvider.getCredentials();
	}
}
