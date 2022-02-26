package com.ksoot.common.boot.config.pagination;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class PaginatedResource<T> implements Iterable<T> {

	private final Collection<T> content;

	private PaginationData metadata;

	public PaginatedResource(final Page<T> page) {
		this.content = page.getContent();
		this.metadata = PaginationData.of(page.getPageable(), page.getTotalElements());
	}

	public PaginatedResource(final Collection<T> content, final Pageable pageable, final long totalRecords) {
		this.content = content;
		this.metadata = PaginationData.of(pageable, totalRecords);
	}

	@JsonProperty("page")
	public PaginationData getMetadata() {
		return this.metadata;
	}

	@JsonProperty("content")
	public Collection<T> getContent() {
		return Collections.unmodifiableCollection(this.content);
	}

	@Override
	public Iterator<T> iterator() {
		return this.content.iterator();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null || !getClass().equals(obj.getClass())) {
			return false;
		}

		PaginatedResource<?> that = (PaginatedResource<?>) obj;
		boolean metadataEquals = this.metadata == null ? that.metadata == null : this.metadata.equals(that.metadata);

		return metadataEquals ? super.equals(obj) : false;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result += this.metadata == null ? 0 : 31 * this.metadata.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return String.format("PagedResource { content: %s, metadata: %s}", getContent(), this.metadata);
	}

}
