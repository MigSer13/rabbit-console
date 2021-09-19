package ru.myhome.serg;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class SenderApp {
        private static final String EXCHANGE_NAME = "exchange_publish";

    public static void main(String[] args) throws Exception{
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        try(Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            Scanner scanner = new Scanner(System.in);
        ){
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
            String key = "";
            String message = "some message";
            System.out.println("Enter post topic: ");
            while (true){
                if(scanner.hasNextLine()){
                    //key = "php";
                    key = scanner.nextLine();
                    break;
                }
                System.out.println("Incorrect input.");
                System.out.println("Enter post topic: ");
            }

            channel.basicPublish(EXCHANGE_NAME, key, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("New publication on the topic " + '"' + key + '"' + " - " + message);
            }
        }
}
