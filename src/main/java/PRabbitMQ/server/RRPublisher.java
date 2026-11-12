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
            System.out.println("Introduce o porto a ser empregado:");
            String port = br.readLine();
            if (hostName == null || hostName.trim().isEmpty()) {
                System.out.println(" [x] O host non pode estar baleiro. Téntao de novo.");
                continue;
            }
            if(port == null || port.trim().isEmpty()) {
                System.out.println(" [x] O porto non pode estar baleiro. Téntao de novo.");
                continue;
            }
            int portNum;
            try {
                portNum = Integer.parseInt(port.trim());
                if(portNum<1 || portNum>65535) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e){
                System.out.println(" [x] Porto non válido.");
                continue;
            }
            try {
                factory.setHost(hostName.trim());
                factory.setPort(portNum);
                // Intentamos establecer a conexión; se o host é inválido ou non accesible, lanzará unha excepción.
                connection = factory.newConnection();
                channel = connection.createChannel();

                // Declaramos un exchange de tipo fanout para enviar as mensaxes á cola de cada consumidor
                channel.exchangeDeclare("rr_exchange", "fanout");

                conectado = true;
                System.out.println(" [x] Conectado exitosamente a " + hostName.trim());

            } catch (Exception e) {
                System.out.println(" [!] Non se puido conectar a '" + hostName.trim() + "'. Motivo: " + e.getMessage());
                System.out.println(" [!] Por favor, comproba o valor e téntao de novo.\n");
            }
            // Pechamos calquera conexión parcial antes de reintentar.
            if (channel != null && channel.isOpen()) {
                channel.close();
            }

            if (connection != null && connection.isOpen()) {
                connection.close();
            }

            channel = null;
            connection = null;
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
