import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;

public class MySingletonConnection {
    private static final MySingletonConnection INSTANCE = new MySingletonConnection();
    private static final String QUEUE_NAME = "data";
    private static Channel channel;
    private static Connection connection;

    private MySingletonConnection() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");
        factory.setHost("rabbitmq"); // localhost
        factory.setPort(5672);

        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static MySingletonConnection getInstance() {
        return INSTANCE;
    }

    public static void sendMessage(String msg) {
        try {
            channel.basicPublish("", QUEUE_NAME, null, msg.getBytes(StandardCharsets.UTF_8));
            Thread.sleep(200);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void closeConnection() {
        try {
            channel.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
