import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public interface IncidentResponseCoordinator {

    /** Registers a host */
    void registerInfectedHost(String hostId);

    /** Retruna true si el host ha sido afectado */
    boolean isHostRegistered(String hostId);

    /** Retorna el numero total de hosts que han sido afectados */
    int getInfectedHostCount();

    /** Interruptor kill para atender el incidente. */
    void activateKillSwitch();

    /** Retorna trye si el kill switch ha sido activado*/
    boolean isKillSwitchActivated();
}

// class
final class RansomwareIncidentCoordinator implements IncidentResponseCoordinator {

    private static class Holder {
        private static final RansomwareIncidentCoordinator INSTANCE = new RansomwareIncidentCoordinator();
    }

    private final Set<String> infectedHosts;
    private final AtomicBoolean killSwitchActive;

    // Private constructor.
    private RansomwareIncidentCoordinator() {
        this.infectedHosts = Collections.synchronizedSet(new HashSet<>());
        this.killSwitchActive = new AtomicBoolean(false);
    }
    // Metodo estatico de tipo RansomwareIncidentCoordiantor
    public static RansomwareIncidentCoordinator getInstance() {
        return Holder.INSTANCE;
    }
