package de.db.sus.omanmc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Delivery;
import de.db.sus.inttest.util.model.ic.InfoCtxMeta;
import de.db.sus.inttest.util.model.ic.InfoCtxMetaIcc;
import de.db.sus.omanmc.webflux.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.QueueSpecification;
import reactor.rabbitmq.RabbitFlux;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.Sender;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.util.Optional;

import static de.db.sus.omanmc.Application.OMAN_IN_QUEUE;
import static de.db.sus.omanmc.Application.OMAN_OUT_EXCHANGE;

@Component
public class Flow {

    final Sender sender;
    final Flux<Delivery> dabFlux;//https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html
    private static final Logger LOGGER = LoggerFactory.getLogger(Flow.class);
    Receiver receiver = new Receiver();

    Flow(Sender sender, Flux<Delivery> dabFlux) {
        this.sender = sender;
        this.dabFlux = dabFlux;
    }

    @Autowired
    private Client client;

    private ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void run() throws JsonProcessingException {

        String j = "{\n" +
                "  \"version\": \"1.12.23\",\n" +
                "  \"prio\": 1,\n" +
                "  \"abonnementtyp\": \"ZUGANZEIGER\",\n" +
                "  \"abonnementid\": \"b374d801-57cb-40a6-a2ef-30ee8959b998\",\n" +
                "  \"scheduledTime\": \"2022-04-15T07:57:38.885Z\",\n" +
                "  \"validUntil\": \"2022-04-15T10:44:18.885Z\",\n" +
                "  \"renderingParameters\": {\n" +
                "    \"renderAsZugtafel\": true,\n" +
                "    \"showsMTFLayout\": true\n }" +
                "  } "
                ;

        String abonnementid = objectMapper.readValue(j, InfoCtxMeta.class).getAbonnementid();


        dabFlux
                .map(m -> {
                    String icc = new String(m.getBody()).replace("'","\"");
                    try {
                        Optional<String> abonnementid1 = Optional.of(objectMapper.readValue(icc, InfoCtxMeta.class).getAbonnementid());
                        LOGGER.info("Received message {}", abonnementid1);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    LOGGER.info("Received message {}", icc);
                    return abonnementid;
                })
                .map()
                .subscribe();


//        receiver.consumeAutoAck(OMAN_IN_QUEUE)
//                .doOnNext(delivery -> {
//                    byte[] body = delivery.getBody();
//                    String s = String.valueOf(body);
//                    LOGGER.info("body {}", s);
//                })
//                .subscribe()
//        ;



//        dabFlux.map(iccContext -> {
//                    try {
//                        String content = String.valueOf(iccContext.getBody());
//                        LOGGER.info("abonnementId {}", content);
//                        Optional<String> abonnementid1 = Optional.of(objectMapper.readValue(content, InfoCtxMeta.class).getAbonnementid());
//                        return abonnementid1;
//                    } catch (IOException e) {
//                        e.printStackTrace();//exception handler !!!!!!!
//                    }
//                    return Optional.empty();
//                })
//                .doOnNext(aboId -> {
//
//                    LOGGER.info("Received response {}", aboId.get());
//                    Mono<String> message = client.getDevices((String) aboId.
//                            get());
//                    LOGGER.info("Received response {}", new String(message.toString()));
//                })
//                .doOnNext(m -> {
//                    sender.send(
//                            Flux.range(1, 1)
//                                    .map(i -> new OutboundMessage("", OMAN_OUT_EXCHANGE, ("Message_" + i).getBytes()))
//                    );
//                    LOGGER.info("Send message to tdc-ses", new String(m.toString()));
//                })
//                .subscribe();
    }

    private boolean filterAboId(Delivery iccContext) {
        return false;
    }

}


//rabbitmqadmin publish exchange=irisplus.oman.ctrl.in.exchange.it routing_key=test payload="{'version':'1.12.23','prio':1,'abonnementtyp':'ZUGANZEIGER','abonnementid':'b374d801-57cb-40a6-2ef-30ee8959b998','scheduledTime':'2022-04-15T07:57:38.885Z','validUntil':'2022-04-15T10:44:18.885Z','renderingParameters':{'renderAsZugtafel':true,'showsMTFLayout':true}}"


//
//{
//        "meta": {
//        "version": "1.12.23",
//        "prio": 1,
//        "abonnementtyp": "ZUGANZEIGER",
//        "abonnementid": "b374d801-57cb-40a6-a2ef-30ee8959b998",
//        "scheduledTime": "2022-04-15T07:57:38.885Z",
//        "validUntil": "2022-04-15T10:44:18.885Z",
//        "renderingParameters": {
//        "renderAsZugtafel": true,
//        "showsMTFLayout": true
//        },
//        "icc": {
//        "id": "38a7adac-04ef-4025-8284-3366068037fd",
//        "created": "2022-04-15T07:57:38.885Z",
//        "version": "v3",
//        "renderingAction": "OUTPUT"
//        },
//        "ic": {
//        "id": "849c3b3a-9563-423f-9999-142e77ceba6b",
//        "created": "2022-04-15T07:57:38.885Z"
//        }
//        },
//        "body": "QXNoIG5hemcgZHVyYmF0dWzDu2ssDQphc2ggbmF6ZyBnaW1iYXR1bCwNCmFzaCBuYXpnIHRocmFrYXR1bMO7aw0KYWdoIGJ1cnp1bS1pc2hpIGtyaW1wYXR1bA=="
//        }













