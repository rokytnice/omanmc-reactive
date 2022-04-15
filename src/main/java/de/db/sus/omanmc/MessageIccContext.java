package de.db.sus.omanmc;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;

public class MessageIccContext {


        private final Envelope _envelope;
        private final AMQP.BasicProperties _properties;
        private final byte[] _body;

        public MessageIccContext(Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
            _envelope = envelope;
            _properties = properties;
            _body = body;
        }

        public Envelope getEnvelope() {
            return _envelope;
        }

        public AMQP.BasicProperties getProperties() {
            return _properties;
        }

        public byte[] getBody() {
            return _body;
        }

        public  byte[]  getAboId() {
            return _body;
        }






}
