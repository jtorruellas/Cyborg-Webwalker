import java.util.*;

public class RunnerCard extends Card {

    private int strength = 0;
    private int virusCounters = 0;
    private boolean rezzed = false;
    public int armitageCreds = 12;

    public RunnerCard() {
        super.setName(new String());
    }

    public void addVirusCounter() {
        virusCounters++;
    }
    public void clearVirusCounters() {
        virusCounters = 0;
    }
    public int getVirusCounters() {
        return virusCounters;
    }
}