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

package com.ksoot.common.boot.config.pagination;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.PageRequest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Rajveer Singh
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@ConditionalOnProperty(prefix = "application.pagination", name = "enabled", havingValue = "true")
@ConfigurationProperties(prefix = "application.pagination")
@Valid
public class PaginationProperties {

	/**
	 * Whether or not to enable Pagination
	 */
	private boolean enabled = false;

	private boolean firstAndLastRels = false;

	@Positive
	private int defaultPageSize = 16;

	@Positive
	private int maxPageSize = 2000;
	
	@NotEmpty
	private String pageParamName = "page";

	@NotEmpty
	private String pageParamDescription = "Requested Page number. zero-based page index, must not be negative";

	@NotEmpty
	private String sizeParamName = "size";

	@NotEmpty
	private String sizeParamDescription = "Size of a page. Allowed between 1 and %d, must not be negative";

	@NotEmpty
	private String sortParamName = "sort";

	@NotEmpty
	private String sortParamDescription = "Sorting criteria in the format: property(,asc|desc). "
			+ "Default sort order is ascending. Multiple sort criteria are supported.";
	
	public String getPageParamDescription() {
		return String.format(this.pageParamDescription);
	}

	public String getSizeParamDescription() {
		return String.format(this.sizeParamDescription, this.maxPageSize);
	}
	
	public int defaultPageNumber() {
		return 0;
	}

	public PageRequest defaultPage() {
		return PageRequest.of(0, this.defaultPageSize);
	}

}
