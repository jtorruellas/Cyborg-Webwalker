import java.util.*;

public class CorpCard extends Card {

    private int advancement;
    private boolean isAdvanceable = false;
    private int trashCost = 99;
    private int scoreValue = 0;
    private int strength = 0;
    private int subroutines = 0;
    private boolean rezzed = false;
    public int adonisCreds = 12;

    public CorpCard() {
        super.setName(new String());
        advancement = 0;
        isAdvanceable = false;
        trashCost = 99;
        scoreValue = 0;
        strength = 0;
        subroutines = 0;
    }

    public void advance(int counters) {
        advancement = advancement + counters;
    }
    public boolean isIce() {
        if ("ICE".equals(type)) {
            return true;
        } else {
            return false;
        }
    }
    public boolean isRegion() {
        if ("Region".equals(subType)) {
            return true;
        } else {
            return false;
        }
    }
    public boolean isAsset() {
        if ("Asset".equals(type)) {
            return true;
        } else {
            return false;
        }
    }
    public boolean isUpgrade() {
        return "Upgrade".equals(subType);
    }
    public boolean isOperation() {
        return "Operation".equals(subType);
    }
    public boolean isMoneyAsset() {
        return "Money Asset".equals(subType);
    }
    public boolean isAgenda() {
        return "Agenda".equals(type);
    }
    public boolean isTrap() {
        return "Trap".equals(subType);
    }
    public void advance() {
        advancement++;
    }
    public int getAdvancement() {
        return advancement;
    }
    public int getScoreValue() {
        return scoreValue;
    }
    public boolean needsIce() {
        return attributes.contains("Ice");
    }
    public boolean isAdvanceable() {
        return attributes.contains("Advanceable");
    }
    public boolean isRezzed() {
        return rezzed;
    }
    public void setTrashCost(int cost) {
        trashCost = cost;
    }
    public void setScoreValue(int value) {
        scoreValue = value;
    }
    public void setSubroutines(int value) {
        scoreValue = value;
    }
    public void setStrength(int value) {
        scoreValue = value;
    }
    public String getName() {
        if (rezzed || ("Operation").equals(type)) {
            return name;
        } else {
            if (("ICE").equals(type)) {
                return "ICE";
            } else {
                return "Asset";
            }
        }
    }
    public String getActualName() {
            return name;
    }
    public int getTrashCost() {
            return trashCost;
    }
    public void rez() {
        rezzed = true;
    }
    public void derez() {
        rezzed = false;
    }
}