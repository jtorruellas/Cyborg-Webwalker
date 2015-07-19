import java.util.*;

public class Server {
    private List<CorpCard> ice = new ArrayList<CorpCard>();
    private CorpCard asset = null;
    private List<CorpCard> assets = new ArrayList<CorpCard>();
    private List<CorpCard> upgrades = new ArrayList<CorpCard>();
    private boolean hasRegion = false;
    private boolean reservedForAgenda = false;
    private String name = "Remote";

    public Server(CorpCard card) {
        if (card.isIce()) {
            ice.add(card);
        } else if (card.isAsset() || card.isAgenda()) {
            assets.add(card);
            if (!card.isUpgrade()) {
                asset = card;
            }
        }
    }
    public Server (String centralType) {
        this.name = centralType;
    }

    public List<CorpCard> getIce() {
        return ice;
    }
    public String getName() {
        return name;
    }
    public List<CorpCard> getAssets() {
        return assets;
    }
    public List<CorpCard> getUpgrades() {
        return upgrades;
    }
    public void setAssets(List<CorpCard> cards) {
        assets = new ArrayList<CorpCard>(cards);
    }
    public CorpCard getAsset() {
        return asset;
    }
    public boolean hasRegion() {
        return this.hasRegion;
    }
    public boolean isRnD() {
        return "RnD".equals(name);
    }
    public boolean isHQ() {
        return "HQ".equals(name);
    }
    public boolean isArchives() {
        return "Archives".equals(name);
    }
    public boolean isRemote() {
        return "Remote".equals(name);
    }
    public boolean reservedForAgenda() {
        return reservedForAgenda;
    }
    public void reserveForAgenda() {
        reservedForAgenda = true;
    }
    public void removeAsset() {
        asset = null;
    }
    public void addCard(CorpCard card) {
        if (card.isIce()) {
            ice.add(card);
        } else if (card.isAsset() || card.isAgenda()) {
            assets.add(card);
            if (!card.isUpgrade()) {
                asset = card;
            } else {
                upgrades.add(card);
                reservedForAgenda = card.reservedForAgenda();
            }
        }
    }

}