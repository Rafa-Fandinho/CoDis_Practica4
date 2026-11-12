package PRabbitMQ.client;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class RRSubscriber {
    private Connection connection;
    private Channel channel;
    private String cola;
    private String consumo;
    private final DataBuffer buffer;

    public RRSubscriber(DataBuffer buffer) throws Exception{
        this.buffer = buffer;
        InputStreamReader is = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(is);
        ConnectionFactory factory = new ConnectionFactory();
        factory.setAutomaticRecoveryEnabled(true);
        factory.setNetworkRecoveryInterval(5000);
        factory.setTopologyRecoveryEnabled(true);
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

                conectado = true;
                System.out.println(" [x] Conectado exitosamente a " + hostName.trim());

            } catch (Exception e) {
                System.out.println(" [x] Non se puido conectar a '" + hostName.trim() + "'. Motivo: " + e.getMessage());
                System.out.println(" [x] Por favor, comproba os valores e téntao de novo.\n");
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

    public void comezarConsumo() throws Exception {
        //Para comezar, cancelamos calquer subscripción anterior
        if (consumo != null){
            pararConsumo();
        }
        // Declaramos unha cola para cada consumidor, que estará asociado ao exchange
        cola = channel.queueDeclare("", false, true, false, null).getQueue();
        channel.queueBind(cola, "rr_exchange", "");
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            try {
                double rr = Double.parseDouble(message);
                System.out.println(" [x] Recibido '" + rr + "'");
                buffer.actualizar(rr);
            } catch (NumberFormatException e) {
                System.out.println(" [x] Dato RR inválido: " + message);
            }
        };
        consumo = channel.basicConsume(cola, true, deliverCallback, tag -> {});
        System.out.println(" [x] Subscripción iniciada.");
    }

    public void pararConsumo() throws Exception {
        if(consumo != null && channel.isOpen()){
            channel.basicCancel(consumo);
            consumo = null;
            if(cola != null){   //Eliminamos a cola cando rematamos de usala, para que non acumule mensaxes
                channel.queueDelete(cola);
                cola = null;
            }
            System.out.println(" [x] Subscripción finalizada.");
        }
    }

    public void termino() {
        try{
            if (consumo != null && channel != null && channel.isOpen()) {
                pararConsumo();
            }
            if (channel != null && channel.isOpen()){
                channel.close();
            }
            if (connection != null && connection.isOpen()){
                connection.close();
            }
        } catch (Exception e) {
            System.err.println("Erro ao pechar o cliente: " + e.getMessage());
        }
    }
}
