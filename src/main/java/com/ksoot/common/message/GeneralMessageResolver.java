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

package com.ksoot.common.message;

/**
 * @author Rajveer Singh
 */
public enum GeneralMessageResolver implements MessageResolver {

	// @formatter:off
	RECORD_CREATED("record.created.successfully", "Record created successfully"),
	RECORD_UPDATED("record.updated.successfully", "Record updated successfully"),
	RECORD_DELETED("record.deleted.successfully", "Record deleted successfully"),
	NO_RECORDS_FOUND("no.records.found", "No records found matching selection criteria"),
	NO_RECORD_FOUND("no.record.found", "No such record exists");
	// @formatter:on

	private String messageCode;

	private String defaultMessage;

	private GeneralMessageResolver(final String messageCode, final String defaultMessage) {
		this.messageCode = messageCode;
		this.defaultMessage = defaultMessage;
	}

	@Override
	public String messageCode() {
		return this.messageCode;
	}

	@Override
	public String defaultMessage() {
		return this.defaultMessage;
	}

}
