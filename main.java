import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public interface IncidentResponseCoordinator {

    /** Registers a host as infected. */
    void registerInfectedHost(String hostId);

    /** Returns true if the host has already been registered as infected. */
    boolean isHostRegistered(String hostId);

    /** Returns the total number of distinct infected hosts registered so far. */
    int getInfectedHostCount();

    /** Triggers the global kill-switch to contain the incident. */
    void activateKillSwitch();

    /** Returns true once the kill-switch has been activated. */
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

    // Metodo estatico de tipo RansomwareIncidentCoordinator
    public static RansomwareIncidentCoordinator getInstance() {
        return Holder.INSTANCE;
    }

    // Registrar host infectado
    @Override
    public void registerInfectedHost(String hostId) {
        boolean isNew = infectedHosts.add(hostId);
        if (isNew) {
            System.out.println("Se ha registrado un nuevo host infectado: " + hostId);
            // Umbral de ejemplo: si hay 3 o mas hosts infectados, activar el kill-switch.
            if (infectedHosts.size() >= 3 && !killSwitchActive.get()) {
                activateKillSwitch();
            }
        }
    }

    // Comprobar si hay un host registrado
    @Override
    public boolean isHostRegistered(String hostId) {
        return infectedHosts.contains(hostId);
    }

    // Contar la cantidad de hosts
    @Override
    public int getInfectedHostCount() {
        return infectedHosts.size();
    }

    // Activar el kill switch
    @Override
    public void activateKillSwitch() {
        if (killSwitchActive.compareAndSet(false, true)) {
            System.out.println("[KILL-SWITCH] Activated. Isolating affected hosts...");
        }
    }

    @Override
    public boolean isKillSwitchActivated() {
        return killSwitchActive.get();
    }
}

// Demostracion
class main {
    public static void main(String[] args) {
        // Simulating three independent detection modules, each getting
        // a reference to the SAME coordinator instance.
        IncidentResponseCoordinator fsWatcher = RansomwareIncidentCoordinator.getInstance();
        IncidentResponseCoordinator netSensor = RansomwareIncidentCoordinator.getInstance();
        IncidentResponseCoordinator procMonitor = RansomwareIncidentCoordinator.getInstance();

        fsWatcher.registerInfectedHost("HOST-01");
        netSensor.registerInfectedHost("HOST-02");
        procMonitor.registerInfectedHost("HOST-01"); // duplicate, ignored
        procMonitor.registerInfectedHost("HOST-03"); // triggers threshold -> kill-switch

        System.out.println("Same instance? " + (fsWatcher == netSensor)); // true
        System.out.println("Infected hosts: " + fsWatcher.getInfectedHostCount());
        System.out.println("Kill-switch active: " + fsWatcher.isKillSwitchActivated());
    }
}