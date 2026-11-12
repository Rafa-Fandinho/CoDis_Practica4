package PRabbitMQ.client;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class SubscriptionManager {
    //Encargado de rexistrar a duración e as renovacións de subscripcións

    private final RRSubscriber subscriber;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService reloxo = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> expiracion;
    private final AtomicBoolean pechado = new AtomicBoolean(false);
    private Consumer<String> statusListener = status -> {};

    public SubscriptionManager(RRSubscriber subscriber, DataBuffer buffer) {
        this.subscriber = subscriber;
        reloxo.scheduleAtFixedRate(buffer::avanzar, 1, 1, TimeUnit.SECONDS);
    }

    public void setStatusListener(Consumer<String> listener){
        this.statusListener = listener;
    }

    public void subscribe(int duracion) {
        if(duracion <= 0 || pechado.get()) {
            return;
        }
        executorService.execute(() -> {
            try {
                //Cancelamos o temporizador anterior
                if (expiracion != null) {
                    expiracion.cancel(false);
                }
                //Iniciamos unha nova subscripción
                subscriber.comezarConsumo();
                statusListener.accept("Subscripción activa");
                System.out.println(" [x] Subscripción activada durante " + duracion + " segundos.");
                //Finalmente, establecemos a expiración
                expiracion = executorService.schedule(() -> {
                    try {
                        subscriber.pararConsumo();
                        statusListener.accept("Subscripción inactiva");
                        System.out.println(" [x] O tempo de subscripción expirou.");
                    } catch (Exception e) {
                        System.out.println("Erro ao finalizar subscripción: " + e.getMessage());
                    }
                }, duracion, TimeUnit.SECONDS);
            } catch (Exception e) {
                statusListener.accept("Erro de conexión");
                System.out.println("Erro ao iniciar a subscripción: " + e.getMessage());
            }
        });
    }

    public void termino() {
        if(!pechado.compareAndSet(false,true)) {
            return;
        }
        reloxo.shutdownNow();
        executorService.execute(() -> {
            if(expiracion != null){
                expiracion.cancel(false);
            }
            subscriber.termino();
            statusListener.accept("Desconectado");
        });
        executorService.shutdown();
    }

}
