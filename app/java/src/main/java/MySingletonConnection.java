import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.nio.charset.StandardCharsets;

public class MySingletonConnection {
    private static final MySingletonConnection INSTANCE = new MySingletonConnection();
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
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static MySingletonConnection getInstance() {
        return INSTANCE;
    }

    public static boolean generateQueue(String queueName) {
        try {
            channel.queueDeclare(queueName, false, false, false, null);
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public static void sendMessage(String msg, String queueName) {
        try {
            channel.basicPublish("", queueName, null, msg.getBytes(StandardCharsets.UTF_8));
            Thread.sleep(50);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void closeConnection(String queueName) {
        try {
            channel.queueDelete(queueName);
            channel.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
