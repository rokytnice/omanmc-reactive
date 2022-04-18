package de.db.sus.omanmc.archiv;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
public class RestMock {

	public Mono<ServerResponse> doResponse(ServerRequest request) {
		return ServerResponse.
				ok().
				contentType(MediaType.APPLICATION_JSON)
				.body(
						BodyInserters.fromValue( new Response("Hello, Spring!") )
				);
	}
}
