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

package com.ksoot.common.error;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author Rajveer Singh
 */
// Just a dummy class to be used to display example value in swagger
@ApiModel(value = "errorDetails", description = "Response of an error occurred on server side")
public abstract class ProblemResponse {

	@JsonProperty
	@ApiModelProperty(value = "Title of error such as Remittance Transaction exception etc.", required = true,
			example = "Error category", position = 1)
	private String title;

	@JsonProperty
	@ApiModelProperty(value = "HTTP Status code sach as 200 or 400 etc.", required = true, example = "400",
			position = 2)
	private int status;

	@JsonProperty
	@ApiModelProperty(value = "The rest API path where the error has occurred such as /api/v1/prices", required = true,
			example = "/api/v1/prices", position = 3)
	private String instance;

	@JsonProperty
	@ApiModelProperty(value = "The text message in EN Locale to be displayed to user specifying the cause of error",
			required = true, example = "Invalid currency code: NUMR", position = 4)
	private String message;

	@JsonProperty
	@ApiModelProperty(value = "The localized text message to be displayed to user specifying the cause of error",
			required = true, example = "Invalid currency code: NUMR", position = 5)
	private String localizedMessage;

	@JsonProperty
	@ApiModelProperty(value = "The timestamp when the error has occurred", required = true,
			example = "Invalid currency code: NUMR", position = 6)
	private OffsetDateTime timestamp;

}
