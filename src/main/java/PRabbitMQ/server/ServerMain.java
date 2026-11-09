package PRabbitMQ.server;

import java.io.BufferedReader;
import java.io.FileReader;

public class ServerMain {
    public static void main(String[] argv) throws Exception {
        //Nun bucle, chama ao lector de arquivos, obtén o dato, publícao, e espera un segundo
        //O bucle remata cando o file reader acada o final do arquivo ou se recibimos un ctrl+C
        try (BufferedReader reader = new BufferedReader(new FileReader("data/rr1.txt"))) {
            RRFileReader lector = new RRFileReader(reader);
            RRPublisher publisher = new RRPublisher();
            publisher.inicializacion();
            double rr = lector.consumir();
            System.out.println(" [x] Comezando a transmisión de datos");
            while(rr !=-1){
                publisher.envio(rr);
                rr = lector.consumir();
                Thread.sleep(1000);
            }
            System.out.println(" [x] Rematando a transmisión de datos");
            publisher.termino();
        } catch (Exception e) {
            System.err.println("Erro na execución do servidor: " + e.getMessage());
        }
    }
}
