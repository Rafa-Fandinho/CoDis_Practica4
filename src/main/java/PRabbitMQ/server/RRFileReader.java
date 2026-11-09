package PRabbitMQ.server;

import java.io.BufferedReader;
import java.io.IOException;

public class RRFileReader {
    private final BufferedReader lector;

    public RRFileReader(BufferedReader lector) {
        this.lector = lector;
    }

    public Double consumir() throws IOException {
        String linea = lector.readLine();
        double rr;
        if (linea !=null) {
            rr = Double.parseDouble(linea);  //Como sabemos os contidos do arquivo, non fai falta facer comprobacións aquí
        }
        else{
            rr = -1;    //Podemos usar -1 como valor de saída pois a frecuencia cardíaca nunca será negativa
        }
        return rr;
    }
}
