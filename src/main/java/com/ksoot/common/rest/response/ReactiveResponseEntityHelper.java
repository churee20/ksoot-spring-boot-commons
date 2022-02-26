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

package com.ksoot.common.rest.response;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import com.ksoot.common.rest.response.builder.AbstractResponseBuilder;
import com.ksoot.common.rest.response.builder.MessageBuilder;
import com.ksoot.common.rest.response.builder.StatusBuilder;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Rajveer Singh
 */
public class ReactiveResponseEntityHelper {

	public static <T> StatusBuilder<T, Mono<ResponseEntity<T>>> of(final T body) {
		Assert.notNull(body, "Body must not be null");
		return new MonoResponseEntityBuilder<T>(body);
	}
	
	public static <T> MessageBuilder<T, Mono<ResponseEntity<T>>> of(final Optional<T> body) {
		Assert.notNull(body, "Body must not be null");
		return of(Mono.justOrEmpty(body));
	}
	
	public static <T> MessageBuilder<T, Mono<ResponseEntity<T>>> of(final Mono<T> body) {
		Assert.notNull(body, "Body must not be null");
		return body.map(content -> new MonoResponseEntityBuilder<T>(content, HttpStatus.OK))
				.defaultIfEmpty(new MonoResponseEntityBuilder<T>(HttpStatus.NOT_FOUND)).block();
	}
	
	public static <T> MessageBuilder<List<T>, Mono<ResponseEntity<List<T>>>> of(final Flux<T> body) {
		Assert.notNull(body, "Body must not be null");
		return body.collectList().map(content -> new FluxResponseEntityBuilder<T>(content, HttpStatus.OK))
				.defaultIfEmpty(new FluxResponseEntityBuilder<T>(HttpStatus.NOT_FOUND)).block();
	}
	
	public static <T> MessageBuilder<T, Mono<ResponseEntity<T>>> accepted() {
		return new MonoResponseEntityBuilder<T>(HttpStatus.ACCEPTED);
	}

	public static <T> MessageBuilder<T, Mono<ResponseEntity<T>>> badRequest() {
		return new MonoResponseEntityBuilder<T>(HttpStatus.BAD_REQUEST);
	}

	public static <T> MessageBuilder<T, Mono<ResponseEntity<T>>> noContent() {
		return new MonoResponseEntityBuilder<T>(HttpStatus.NO_CONTENT);
	}

	public static <T> MessageBuilder<T, Mono<ResponseEntity<T>>> notFound() {
		return new MonoResponseEntityBuilder<T>(HttpStatus.NOT_FOUND);
	}

	public static <T> MessageBuilder<T, Mono<ResponseEntity<T>>> created(final URI location) {
		return new MonoResponseEntityBuilder<T>(location);
	}

	public static <T> MessageBuilder<T, Mono<ResponseEntity<T>>> created(final URI location, final T body) {
		return new MonoResponseEntityBuilder<T>(location, body);
	}

	public static <T> MessageBuilder<T, Mono<ResponseEntity<T>>> ok(final T body) {
		return new MonoResponseEntityBuilder<T>(body, HttpStatus.OK);
	}

	public static <T> MessageBuilder<T, Mono<ResponseEntity<T>>> ok() {
		return new MonoResponseEntityBuilder<T>(HttpStatus.OK);
	}
	
	public static class MonoResponseEntityBuilder<T> extends AbstractResponseBuilder<T, Mono<ResponseEntity<T>>> {

		private URI location;
		
		MonoResponseEntityBuilder(final HttpStatus status) {
			super(status);
		}

		MonoResponseEntityBuilder(final T body) {
			super(body);
		}

		MonoResponseEntityBuilder(final T body, final HttpStatus status) {
			super(body, status);
		}

		MonoResponseEntityBuilder(final URI location) {
			super(HttpStatus.CREATED);
			Assert.notNull(location, "location must not be null");
			this.location = location;
		}

		MonoResponseEntityBuilder(final URI location, final T body) {
			super(body, HttpStatus.CREATED);
			Assert.notNull(location, "location must not be null");
			Assert.notNull(body, "body must not be null");
			this.location = location;
		}

		@Override
		public Mono<ResponseEntity<T>> build() {
			org.springframework.http.ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.status(this.status);
			if(this.location != null) {
				bodyBuilder.location(this.location);
			}
			if(this.headers != null) {
				bodyBuilder.headers(this.headers);
			}
			return this.body != null ? Mono.just(bodyBuilder.body(this.body)) : Mono.just(bodyBuilder.build());
		}
	}

	public static class FluxResponseEntityBuilder<T> extends AbstractResponseBuilder<List<T>, Mono<ResponseEntity<List<T>>>> {

		private URI location;
		
		FluxResponseEntityBuilder(final HttpStatus status) {
			super(status);
		}

		FluxResponseEntityBuilder(final List<T> body) {
			super(body);
		}

		FluxResponseEntityBuilder(final List<T> body, final HttpStatus status) {
			super(body, status);
		}

		FluxResponseEntityBuilder(final URI location) {
			super(HttpStatus.CREATED);
			Assert.notNull(location, "location must not be null");
			this.location = location;
		}

		@Override
		public Mono<ResponseEntity<List<T>>> build() {
			org.springframework.http.ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.status(this.status);
			if(this.location != null) {
				bodyBuilder.location(this.location);
			}
			if(this.headers != null) {
				bodyBuilder.headers(this.headers);
			}
			return this.body != null ? Mono.just(bodyBuilder.body(this.body)) : Mono.just(bodyBuilder.build());
		}
	}
	
}
