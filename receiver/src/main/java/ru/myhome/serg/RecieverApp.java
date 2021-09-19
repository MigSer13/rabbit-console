package ru.myhome.serg;

import com.rabbitmq.client.*;

import java.io.IOException;

public class RecieverApp {
    private static final String EXCHANGE_NAME = "exchange_publish";

    public static void main(String[] args) throws Exception{
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection = connectionFactory.newConnection();

        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        String queueName = channel.queueDeclare().getQueue();
        String key = "php.#";
        channel.queueBind(queueName, EXCHANGE_NAME, key);
        System.out.println("Waiting for mailing by " + key);

        DeliverCallback deliverCallback = new DeliverCallback() {
            @Override
            public void handle(String tag, Delivery delivery) throws IOException {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println("- received a new article on "
                        + delivery.getEnvelope().getRoutingKey() + ": "
                        + message);
            }
        };

        CancelCallback cancelCallback = new CancelCallback() {
            @Override
            public void handle(String consumerTag) throws IOException {

            }
        };
        channel.basicConsume(queueName, true, deliverCallback, cancelCallback);

    }
}
