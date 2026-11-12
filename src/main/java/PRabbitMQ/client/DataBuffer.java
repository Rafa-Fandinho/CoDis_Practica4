package PRabbitMQ.client;

import java.util.ArrayList;

public class DataBuffer {

    private static final int WINDOW_SIZE = 60;
    //Mantén e actualiza un array de doubles para o display de datos
    private final ArrayList<Double> buffer = new ArrayList<>();

    public DataBuffer() {
        for(int i=0; i<WINDOW_SIZE; i++){
            buffer.add(0.0);
        }
    }

    public synchronized ArrayList<Double> getBuffer() {return new ArrayList<>(buffer);}

    public synchronized void avanzar(){
        buffer.remove(0);
        buffer.add(0.0);
    }
    public synchronized void actualizar(double rr){
        buffer.set(WINDOW_SIZE-1, rr);  //Isto non vai a solapar datos xa que o servidor os manda con un segundo de diferenza
    }
    //A idea desta función é que sempre que non haxa datos se establecerán a cero, e os datos que vaian chegando para cada segundo substituirán aos ceros.
    //Teño que facer tamén que despois dunha subscripción, os datos se vaian mandando como 0s tamén cada segundo
    //De xeito que na posición N do array, haxa o dato recibido fai WINDOW_SIZE-N segundos
}
