package com.ksoot.common.boot.config.pagination;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.UriTemplate;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.web.util.UriComponentsBuilder.fromUri;

public class PaginatedResourceAssembler {

	private final HateoasPageableHandlerMethodArgumentResolver pageableResolver;

	private boolean forceFirstAndLastRels;

	public PaginatedResourceAssembler(@Nullable HateoasPageableHandlerMethodArgumentResolver resolver,
			boolean forceFirstAndLastRels) {
		this.pageableResolver = resolver == null ? new HateoasPageableHandlerMethodArgumentResolver() : resolver;
		this.forceFirstAndLastRels = forceFirstAndLastRels;
	}

	/**
	 * Use in case of reactive web applications
	 * @param <T> Class of the content of page
	 * @param content Page data
	 * @param pageable Requested Page
	 * @param totalRecords Total number of records
	 * @param requestUri Current request URI
	 * @return PaginatedResource
	 */
	public <T> PaginatedResource<T> assemble(final List<T> content, final Pageable pageable, final long totalRecords,
			final URI requestUri) {
		return assemble(new PageImpl<T>(content, pageable, totalRecords), UriTemplate.of(requestUri.toString()));
	}

	/**
	 * Use in case of non reactive web application, base URL can be automatically
	 * determined from ServletUriComponentsBuilder
	 * @param <T> Class of the content of page
	 * @param content Page data
	 * @param pageable Requested Page
	 * @param totalRecords Total number of records
	 * @return PaginatedResource
	 */
	public <T> PaginatedResource<T> assemble(final List<T> content, final Pageable pageable, final long totalRecords) {
		return assemble(new PageImpl<T>(content, pageable, totalRecords), getUriTemplate());
	}

	/**
	 * Use in case of non reactive web application, base URL can be automatically
	 * determined from ServletUriComponentsBuilder
	 * @param <T> Class of the content of page
	 * @param page Requested Page
	 * @return PaginatedResource
	 */
	public <T> PaginatedResource<T> assemble(final Page<T> page) {

		Assert.notNull(page, "Page must not be null!");
		Assert.notNull(page.getContent(), "Page Content must not be null!");
		Assert.notNull(page.getPageable(), "Pageable must not be null!");

		PaginatedResource<T> paginatedResource = new PaginatedResource<>(page);
		addPaginationLinks(paginatedResource, page, getUriTemplate());
		return paginatedResource;
	}

	public <T> PaginatedResource<T> assemble(final Page<T> page, final URI requestUri) {
		return assemble(page, UriTemplate.of(requestUri.toString()));
	}

	private <T> PaginatedResource<T> assemble(final Page<T> page, final UriTemplate base) {

		Assert.notNull(page, "Page must not be null!");
		Assert.notNull(page.getContent(), "Page Content must not be null!");
		Assert.notNull(page.getPageable(), "Pageable must not be null!");

		PaginatedResource<T> paginatedResource = new PaginatedResource<>(page);
		addPaginationLinks(paginatedResource, page, base);
		return paginatedResource;
	}

	private <T> void addPaginationLinks(final PaginatedResource<T> paginatedResource, final Page<T> page,
			final UriTemplate base) {

		// UriTemplate base = UriTemplate.of(requestUri.toString());

		boolean isNavigable = page.hasPrevious() || page.hasNext();

		Link selfLink = createLink(base, page.getPageable(), IanaLinkRelations.SELF);

		paginatedResource.getMetadata().add(selfLink);

		if (page.hasPrevious()) {
			paginatedResource.getMetadata().add(createLink(base, page.previousPageable(), IanaLinkRelations.PREV));
		}

		if (page.hasNext()) {
			paginatedResource.getMetadata().add(createLink(base, page.nextPageable(), IanaLinkRelations.NEXT));
		}

		if (isNavigable || this.forceFirstAndLastRels) {
			paginatedResource.getMetadata()
					.add(createLink(base, PageRequest.of(0, page.getSize(), page.getSort()), IanaLinkRelations.FIRST));
		}

		if (isNavigable || this.forceFirstAndLastRels) {

			int lastIndex = page.getTotalPages() == 0 ? 0 : page.getTotalPages() - 1;

			paginatedResource.getMetadata().add(createLink(base,
					PageRequest.of(lastIndex, page.getSize(), page.getSort()), IanaLinkRelations.LAST));
		}
	}

	private Link createLink(UriTemplate base, Pageable pageable, LinkRelation relation) {
		UriComponentsBuilder builder = fromUri(base.expand());
		this.pageableResolver.enhance(builder, null, pageable);
		return Link.of(UriTemplate.of(builder.build().toString()), relation);
	}

	private UriTemplate getUriTemplate() {
		return UriTemplate.of(currentRequest());
	}

	// Work in case of Servlet web application, in case of Reactive web application throws
	// NullPointerException
	private static String currentRequest() {
		return ServletUriComponentsBuilder.fromCurrentRequest().build().toString();
	}

}
