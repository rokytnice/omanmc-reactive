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

import static de.db.sus.omanmc.Application.QUEUE;

@Component
public class Runner  {

    final Sender sender;
    final Flux<Delivery> deliveryFlux;
    private static final Logger LOGGER = LoggerFactory.getLogger(Runner.class);

    Runner(Sender sender, Flux<Delivery> deliveryFlux) {
        this.sender = sender;
        this.deliveryFlux = deliveryFlux;
    }

    @Autowired
    private Client client;


    @PostConstruct
    public void run()  {
        AtomicInteger messageCount = new AtomicInteger(1);
        deliveryFlux.subscribe(m -> {
            LOGGER.info("Received message {}", new String(m.getBody()));
            Mono<String> message = client.getMessage();
            LOGGER.info("Received response {}", new String(message.toString() ));
            messageCount.getAndIncrement();
            sender.send(

                            Flux.range(messageCount.get(), messageCount.get())
                                    .map(i -> new OutboundMessage("",      QUEUE,                    ("Message_" + i).getBytes()))
                    )
                    .subscribe();

        });

        LOGGER.info("Sending messages...");

    }

}