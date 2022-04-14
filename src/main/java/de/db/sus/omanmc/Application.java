
package de.db.sus.omanmc;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Delivery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.*;

import javax.annotation.PreDestroy;

@SpringBootApplication
public class Application {

    static final String OMAN_IN_QUEUE = "irisplus.oman.ctrl.in.queue.it";
    static final String OMAN_OUT_EXCHANGE = "irisplus.oman.ctrl.out.exchange.it";


    @Autowired
    private Mono<Connection> connectionMono;

    @Autowired
    private Runner runner;


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

    }

    // the mono for connection, it is cached to re-use the connection across sender and receiver instances
    // this should work properly in most cases
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
