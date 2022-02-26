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

package com.ksoot.common.boot.config.swagger.customizer;

import java.util.List;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.ksoot.common.boot.config.pagination.PaginationProperties;

import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.schema.CollectionType;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.service.ParameterStyle;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import org.springframework.core.Ordered;
import org.springframework.data.domain.Pageable;

import static com.google.common.collect.Lists.newArrayList;

/**
 * The Springfox Plugin to resolve {@link Pageable} parameter into plain fields.
 *
 * @author Rajveer Singh
 */
public class PageableParameterBuilderPlugin implements OperationBuilderPlugin, Ordered {

	private final PaginationProperties properties;

	private final TypeNameExtractor nameExtractor;

	private final TypeResolver resolver;

	private final ResolvedType pageableType;

	public PageableParameterBuilderPlugin(PaginationProperties properties, TypeNameExtractor nameExtractor, TypeResolver resolver) {
		this.properties = properties;
		this.nameExtractor = nameExtractor;
		this.resolver = resolver;
		this.pageableType = resolver.resolve(Pageable.class);
	}

	@Override
	public boolean supports(DocumentationType delimiter) {
		return SwaggerPluginSupport.pluginDoesApply(delimiter);
	}

	@Override
	public int getOrder() {
		return SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 30;
	}
	
	@Override
	public void apply(OperationContext context) {
		List<RequestParameter> parameters = newArrayList();
		int i = 0;
		for (ResolvedMethodParameter methodParameter : context.getParameters()) {
			ResolvedType resolvedType = methodParameter.getParameterType();
			if (this.pageableType.equals(resolvedType)) {
				ParameterContext parameterContext = new ParameterContext(methodParameter,
						context.getDocumentationContext(), context.getGenericsNamingStrategy(), context, i++);

				parameters.add(createPageParameter(parameterContext));
				parameters.add(createSizeParameter(parameterContext));
				parameters.add(createSortParameter(parameterContext));

				context.operationBuilder().requestParameters(parameters);
			}
		}
	}

	/**
	 * Create a page parameter. Override it if needed. Set a default value for example.
	 * @param context {@link Pageable} parameter context
	 * @return The page parameter
	 */
	protected RequestParameter createPageParameter(ParameterContext context) {
		return new RequestParameterBuilder().name(this.properties.getPageParamName()).in(ParameterType.QUERY)
				.query(q -> q.defaultValue("" + this.properties.defaultPageNumber()).style(ParameterStyle.SIMPLE)
	            .model(m -> m.scalarModel(ScalarType.INTEGER)))
				.description(this.properties.getPageParamDescription()).build();
	}

	/**
	 * Create a size parameter. Override it if needed. Set a default value for example.
	 * @param context {@link Pageable} parameter context
	 * @return The size parameter
	 */
	protected RequestParameter createSizeParameter(ParameterContext context) {
		return new RequestParameterBuilder().name(this.properties.getSizeParamName()).in(ParameterType.QUERY)
				.query(q -> q.defaultValue("" + this.properties.getDefaultPageSize()).style(ParameterStyle.SIMPLE)
	            .model(m -> m.scalarModel(ScalarType.INTEGER)))
				.description(this.properties.getSizeParamDescription()).build();
	}

	protected RequestParameter createSortParameter(ParameterContext context) {
		return new RequestParameterBuilder().name(this.properties.getSortParamName()).in(ParameterType.QUERY)
				.query(q -> q.allowEmptyValue(true).style(ParameterStyle.SIMPLE)
	            .model(m -> m.collectionModel(cm -> cm.collectionType(CollectionType.LIST)
	            		.model(cms -> cms.scalarModel(ScalarType.STRING)))))
				.example(null).description(this.properties.getSortParamDescription()).build();
	}

	TypeResolver getResolver() {
		return this.resolver;
	}

	TypeNameExtractor getNameExtractor() {
		return this.nameExtractor;
	}
}
