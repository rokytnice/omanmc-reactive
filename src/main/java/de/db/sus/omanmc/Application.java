
package de.db.sus.omanmc;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Delivery;
import de.db.sus.omanmc.flux.Flow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.*;

import javax.annotation.PreDestroy;

import static de.db.sus.omanmc.flux.Flow.OMAN_IN_QUEUE;

@SpringBootApplication
public class Application {

    @Autowired
    private Mono<Connection> connectionMono;

    @Autowired
    private Flow sequence;


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean()
    Mono<Connection> connectionMono() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return Mono.fromCallable(() -> connectionFactory.newConnection("reactor-rabbit")).cache();
    }

    @Bean
    Sender sender(Mono<Connection> connectionMono) {
        return RabbitFlux.createSender(new SenderOptions().connectionMono(connectionMono));
    }

    @Bean
    Receiver receiver(Mono<Connection> connectionMono) {
        return RabbitFlux.createReceiver(new ReceiverOptions().connectionMono(connectionMono));
    }

    @Bean
    Flux<Delivery> deliveryFlux(Receiver receiver) {
        return receiver.consumeNoAck(OMAN_IN_QUEUE);
    }


    @PreDestroy
    public void close() throws Exception {
        connectionMono.block().close();
    }

    // a runner that publishes messages with the sender bean and consumes them with the receiver bean

}
