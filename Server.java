import java.awt.*;
import java.text.*;
import java.awt.font.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.util.*;
import java.lang.String;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;



public class Server extends JComponent {
    private int serverNumber;
    private ArrayList<CorpCard> ice = new ArrayList<CorpCard>();
    private CorpCard asset = null;
    private ArrayList<CorpCard> assets = new ArrayList<CorpCard>();
    private ArrayList<CorpCard> upgrades = new ArrayList<CorpCard>();
    private boolean hasRegion = false;
    private boolean reservedForAgenda = false;
    private String name = "Remote";

    public Server(CorpCard card, int serverNumber) {
        this.serverNumber = serverNumber;
        card.setXCoord(30+(serverNumber*225));
        int yCoord = (card.isIce() ? ((ice.size())*225) + 275 : 30);
        card.setYCoord(yCoord);
        card.setServerNumber(serverNumber);
        if (card.isIce()) {
            ice.add(card);
        } else if (card.isAsset() || card.isAgenda()) {
            assets.add(card);
            if (!card.isUpgrade()) {
                asset = card;
            }
        }
    }

    public Server (String centralType, int serverNumber) {
        this.name = centralType;
        this.serverNumber = serverNumber;
        this.asset = new CorpCard(centralType, "Central");
        asset.setXCoord(30+(serverNumber*225));
        asset.setYCoord(30);
        asset.setServerNumber(serverNumber);
    }

    public ArrayList<CorpCard> getIce() {
        return ice;
    }
    public String getName() {
        return name;
    }
    public ArrayList<CorpCard> getAssets() {
        return assets;
    }
    public ArrayList<CorpCard> getUpgrades() {
        return upgrades;
    }
    public void setAssets(ArrayList<CorpCard> cards) {
        assets = new ArrayList<CorpCard>(cards);
    }
    public CorpCard getAsset() {
        return asset;
    }
    public int getServerNumber() {
        return serverNumber;
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
    public void refreshCards(Graphics g) {
        if (!ice.isEmpty()) {
            int i = 0;
            for (CorpCard c : ice) {
                c.draw(g);
            }
        }
        if (asset != null) {
            asset.draw(g);
        }
    }
    public void addCard(CorpCard card) {
        card.setServerNumber(serverNumber);
        if (card.isIce()) {
            card.setXCoord(30 + (serverNumber*225));
            card.setYCoord((ice.size()*175)+275);
            card.setIcePosition(ice.size());
            ice.add(card);
        } else if (card.isAsset() || card.isAgenda()) {
            assets.add(card);
            card.setXCoord(30+((serverNumber*225)+assets.size()*5));
            card.setYCoord(30+(assets.size()*5));
            if (!card.isUpgrade()) {
                asset = card;
            } else {
                upgrades.add(card);
                reservedForAgenda = card.reservedForAgenda();
            }
        }
    }
    public void toggleZoom(int xCoord, int yCoord, boolean zoom) {
        if (asset != null) {
            asset.checkClickLocation(xCoord, yCoord, zoom);
        }
        for (CorpCard c : ice) {
            c.checkClickLocation(xCoord, yCoord, zoom);
        }
    }
    public CorpCard getCardFromCoord (int xCoord, int yCoord){
        if (asset != null && asset.checkClickLocation(xCoord, yCoord)) {
            return asset;
        }
        for (CorpCard c : ice) {
            if (c.checkClickLocation(xCoord, yCoord)) {
                return c;
            }
        }
        return null;
    }
}