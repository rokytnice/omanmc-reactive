package de.db.sus.omanmc;

import com.rabbitmq.client.Delivery;
import de.db.sus.omanmc.webflux.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

import javax.annotation.PostConstruct;

import java.util.concurrent.atomic.AtomicInteger;

import static de.db.sus.omanmc.Application.OMAN_OUT_EXCHANGE;

@Component
public class Runner  {

    final Sender sender;
    final Flux<Delivery> dabFlux;
    private static final Logger LOGGER = LoggerFactory.getLogger(Runner.class);

    Runner(Sender sender, Flux<Delivery> dabFlux) {
        this.sender = sender;
        this.dabFlux = dabFlux;
    }

    @Autowired
    private Client client;


    @PostConstruct
    public void run() {
        AtomicInteger messageCount = new AtomicInteger(1);
        dabFlux.doOnNext(m -> {
                    LOGGER.info("Received message {}", new String(m.getBody()));
                    Mono<String> message = client.getDevices();

        })
                .doOnNext(m -> {
                    LOGGER.info("Received response {}", new String(m.toString()));
                    messageCount.getAndIncrement();
                })
                .doOnNext(m -> {
                    sender.send(
                            Flux.range(messageCount.get(), messageCount.get())
                                    .map(i -> new OutboundMessage("", OMAN_OUT_EXCHANGE,    ("Message_" + i).getBytes()))
                    );
                    LOGGER.info("Send message to tdc-ses", new String(m.toString()));
                })
                .subscribe();
    }

}




















