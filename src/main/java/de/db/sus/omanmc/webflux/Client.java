package de.db.sus.omanmc.webflux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class Client {

	private final WebClient client;
	private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
	// Spring Boot auto-configures a `WebClient.Builder` instance with nice defaults and customizations.
	// We can use it to create a dedicated `WebClient` for our component.
	public Client(WebClient.Builder builder) {
		this.client = builder.baseUrl("http://localhost:8080").build();
	}

	public Mono<String> getDevices(String aboId) {
		LOGGER.info("REST call with aboId {}",aboId );
		return this.
				client
				.get()
//				.uri("/hello").accept(MediaType.APPLICATION_JSON)
				.uri("http://localhost:8085/logicalDevices?aboid="+aboId)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(Response.class)
				.map(Response::getMessage)
				;
	}

}
