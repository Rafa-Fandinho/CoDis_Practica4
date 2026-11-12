package PRabbitMQ.server;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class RRPublisher {
    private Connection connection;
    private Channel channel;

    public RRPublisher() throws Exception {  //Constructor baleiro
        // creamos la conexión al servidor
        InputStreamReader is = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(is);
        ConnectionFactory factory = new ConnectionFactory();
        boolean conectado = false;
        while (!conectado) {
            System.out.println("Introduce a dirección IP ou hostname a ser empregada:");
            String hostName = br.readLine();
            if (hostName == null) {
                throw new java.io.IOException("Non hai entrada dispoñible para o hostname.");
            }

            if (hostName.trim().isEmpty()) {
                System.out.println(" [x] O host non pode estar baleiro. Inténtao de novo");
                continue;
            }
            try {
                factory.setHost(hostName.trim());
                factory.setPort(5672);
                // Intentamos establecer a conexión; se o host é inválido ou non accesible, lanzará unha excepción.
                connection = factory.newConnection();
                channel = connection.createChannel();

                // Declaramos un exchange de tipo fanout para enviar as mensaxes á cola de cada consumidor
                channel.exchangeDeclare("rr_exchange", "fanout");

                conectado = true;
                System.out.println(" [x] Conectado exitosamente a " + hostName.trim());

            } catch (Exception e) {
                System.out.println(
                        " [!] Non se puido conectar a '" +
                                hostName.trim() + "'. Motivo: " + e.getMessage()
                );

                // Pechamos só os recursos creados durante este intento fallido.
                try {
                    if (channel != null && channel.isOpen()) {
                        channel.close();
                    }
                    if (connection != null && connection.isOpen()) {
                        connection.close();
                    }
                } catch (Exception closeError) {
                    System.err.println("Erro durante a limpeza: " + closeError.getMessage());
                }

                channel = null;
                connection = null;

                System.out.println(" [!] Comproba os datos e inténtao de novo.\n");
            }
        }
    }

    public void envio (Double rr) throws Exception {
    String message = String.valueOf(rr);
    channel.basicPublish("rr_exchange", "", null, message.getBytes(StandardCharsets.UTF_8));
    System.out.println(" [x] Enviado '" + message + "'");
    }

    public void termino() {
        try{
            if (channel != null && channel.isOpen()){
                channel.close();
            }
            if (connection != null && connection.isOpen()){
                connection.close();
            }
        } catch (Exception e) {
            System.err.println("Erro ao pechar o servidor: " + e.getMessage());
        }
    }
}
