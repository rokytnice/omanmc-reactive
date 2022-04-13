package de.db.sus.omanmc;

import com.rabbitmq.client.Delivery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

import javax.annotation.PostConstruct;

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

    @PostConstruct
    public void run()  {
        int messageCount = 10;
        deliveryFlux.subscribe(m -> {
            LOGGER.info("Received message {}", new String(m.getBody()));


        });

        LOGGER.info("Sending messages...");
        sender.send(Flux.range(1, messageCount).map(i -> new OutboundMessage("", QUEUE, ("Message_" + i).getBytes())))
                .subscribe();
    }

}