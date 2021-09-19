package ru.myhome.serg;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Scanner;

public class RecieverApp {
    private static final String EXCHANGE_NAME = "exchange_publish";

    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        try (
                Connection connection = connectionFactory.newConnection();
                Channel channel = connection.createChannel();
                Scanner scanner = new Scanner(System.in);
        ) {
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
            String queueName = channel.queueDeclare().getQueue();
            System.out.println("Enter topic of subsribe: ");
            while (true) {
                if (scanner.hasNextLine()){
                    String str = scanner.nextLine().trim().toLowerCase();
                    if (str.equals("exit")) {
                        break;
                    }
                    String[] arrStr = str.split(" ");
                    String key = arrStr[arrStr.length-1];
                    String fullkey = key + ".#";
                if (str.equals("drop_topic " + key)) {
                    channel.queueUnbind(queueName, EXCHANGE_NAME, fullkey);
                    System.out.println("You are unsubscribed from the topic on " + key);
                    System.out.println("Enter topic of subsribe: ");
                }

                if (str.equals("set_topic " + key)) {
                    channel.queueBind(queueName, EXCHANGE_NAME, fullkey);
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
                    System.out.println("Enter new topic of subsribe: ");
                }
            }}
        }
    }
}
