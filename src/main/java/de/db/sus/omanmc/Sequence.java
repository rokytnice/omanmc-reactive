package de.db.sus.omanmc;

import com.rabbitmq.client.Delivery;
import de.db.sus.omanmc.webflux.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

import javax.annotation.PostConstruct;

import java.util.concurrent.atomic.AtomicInteger;

import static de.db.sus.omanmc.Application.OMAN_OUT_EXCHANGE;

@Component
public class Sequence {

    final Sender sender;
    final Flux<Delivery> dabFlux;//https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html
    private static final Logger LOGGER = LoggerFactory.getLogger(Sequence.class);

    Sequence(Sender sender, Flux<Delivery> dabFlux) {
        this.sender = sender;
        this.dabFlux = dabFlux;
    }

    @Autowired
    private Client client;


    @PostConstruct
    public void run() {
        //                    LOGGER.info("Received message {}", new String(m.getBody()));
        dabFlux.map(iccContext ->  iccContext.getBody() )
                .doOnNext(m -> {
                    LOGGER.info("Received response {}", new String(m));
                    Mono<String> message = client.getDevices(String.valueOf(m));
                    LOGGER.info("Received response {}", new String(message.toString()));
                })
                .doOnNext(m -> {
                    sender.send(
                            Flux.range(1, 1)
                                    .map(i -> new OutboundMessage("", OMAN_OUT_EXCHANGE, ("Message_" + i).getBytes()))
                    );
                    LOGGER.info("Send message to tdc-ses", new String(m.toString()));
                })
                .subscribe();
    }

    private boolean filterAboId(Delivery iccContext) {
        return false;
    }

}




















