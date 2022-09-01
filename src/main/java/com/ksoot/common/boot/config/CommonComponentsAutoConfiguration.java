package com.ksoot.common.boot.config;

import static com.ksoot.common.boot.BootConstant.BeanName.APPLICATION_EVENT_MULTICASTER_BEAN_NAME;
import static com.ksoot.common.boot.BootConstant.BeanName.APPLICATION_TASK_EXECUTOR_BEAN_NAME;

import java.util.concurrent.Executor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.web.cors.CorsConfiguration;
import org.zalando.problem.spring.web.advice.ProblemHandling;

import com.ksoot.common.boot.config.error.ProblemProperties;
import com.ksoot.common.boot.config.error.db.ConstraintNameResolver;
import com.ksoot.common.boot.config.error.db.MysqlConstraintNameResolver;
import com.ksoot.common.boot.config.error.db.OracleConstraintNameResolver;
import com.ksoot.common.boot.config.error.db.PostgresConstraintNameResolver;
import com.ksoot.common.boot.config.error.db.SQLServerConstraintNameResolver;
import com.ksoot.common.boot.util.ConfigRuntimeException;
import com.ksoot.common.message.MessageProvider;

@Configuration
@EnableConfigurationProperties(value = { TaskExecutionProperties.class, TaskSchedulingProperties.class,
		ProblemProperties.class })
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class CommonComponentsAutoConfiguration {

	@ConditionalOnMissingBean(name = APPLICATION_EVENT_MULTICASTER_BEAN_NAME)
	@Bean
	public ApplicationEventMulticaster applicationEventMulticaster(
			@Qualifier(APPLICATION_TASK_EXECUTOR_BEAN_NAME) final Executor taskExecutor) {
		SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
		eventMulticaster.setTaskExecutor(taskExecutor);
		return eventMulticaster;
	}

	// If you are using Spring Boot, this is all you need:
	// server.forward-headers-strategy = framework|native|none
	// If you are NOT using Spring Boot, then you must configure following bean:
	// @ConditionalOnMissingBean
	// @Bean
	// public ForwardedHeaderTransformer forwardedHeaderTransformer() {
	// return new ForwardedHeaderTransformer();
	// }

	@ConditionalOnMissingBean(value = MessageProvider.class)
	@Bean
	public MessageProvider messageProvider(final MessageSource messageSource) {
		return new MessageProvider(messageSource);
	}

	@ConditionalOnMissingBean(value = SpringProfiles.class)
	@Bean
	public SpringProfiles springProfiles(final Environment environment) {
		return new SpringProfiles(environment);
	}

	@ConditionalOnMissingBean(value = CorsConfiguration.class)
	@Bean
	@ConfigurationProperties(prefix = "application.cors")
	public CorsConfiguration corsConfiguration() {
		return new CorsConfiguration();
	}

	@ConditionalOnProperty(value = "application.problem.enabled", havingValue = "true")
	@ConditionalOnClass(value = { ProblemHandling.class, Database.class })
	public static class ConstraintNameResolverAutoConfiguration {

		@ConditionalOnMissingBean(value = ConstraintNameResolver.class)
		@Bean
		public ConstraintNameResolver constraintNameResolver(final Environment env) {
			String dbPlateform = env.getProperty("spring.jpa.database");
			if (StringUtils.isEmpty(dbPlateform)) {
				throw new ConfigRuntimeException(
						"Property \"spring.jpa.database\" not found. Please specify database plateform in configurations");
			}
			Database database = Database.valueOf(dbPlateform);

			switch (database) {
			case SQL_SERVER:
				return new SQLServerConstraintNameResolver();
			case POSTGRESQL:
				return new PostgresConstraintNameResolver();
			case MYSQL:
				return new MysqlConstraintNameResolver();
			case ORACLE:
				return new OracleConstraintNameResolver();
			// TODO: Add more cases for other databases constraint name resolver
			// implementations
			default:
				throw new IllegalStateException("constraintNameResolver bean could not be instantiated, "
						+ "add ConstraintNameResolver implementaion for: " + database);
			}
		}

	}

	/*
	 * @Configuration
	 * 
	 * @ConditionalOnClass(JaxbAnnotationModule.class) public static class
	 * MappingJackson2XmlHttpMessageConverterConfiguration {
	 * 
	 * @ConditionalOnMissingBean(value =
	 * MappingJackson2XmlHttpMessageConverter.class)
	 * 
	 * @Bean public MappingJackson2XmlHttpMessageConverter
	 * mappingJackson2XmlHttpMessageConverter() { var jaxbAnnotationModule = new
	 * JaxbAnnotationModule(); var mappingJackson2XmlHttpMessageConverter = new
	 * MappingJackson2XmlHttpMessageConverter();
	 * mappingJackson2XmlHttpMessageConverter.getObjectMapper().registerModule(
	 * jaxbAnnotationModule); return mappingJackson2XmlHttpMessageConverter; } }
	 */
	// @Bean
	// public Slf4jLogger.Level
	// feignLoggerLevel(@Value("${application.feign-logging-level:NONE}") final
	// FeignLoggingLevel feignLoggingLevel) {
	// switch (feignLoggingLevel) {
	// case BASIC:
	// return Slf4jLogger.Level.BASIC;
	// case HEADERS:
	// return Slf4jLogger.Level.HEADERS;
	// case FULL:
	// return Slf4jLogger.Level.FULL;
	// case NONE:
	// return Slf4jLogger.Level.NONE;
	//
	// default:
	// return Slf4jLogger.Level.NONE;
	// }
	// }

}
