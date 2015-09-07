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

public class CorpCard extends Card 
{
    private int advancement;
    private boolean isAdvanceable = false;
    private int trashCost = 99;
    private int scoreValue = 0;
    private int strength = 0;
    private int subroutines = 0;
    private boolean rezzed = false;
    private boolean exposed = false;
    public int adonisCreds = 12;
    public int trapCounter = 0;
    public String hostedCard = "";
    private int counters = 0;
    private BufferedImage bigImage;
    private BufferedImage smallImage;
    private boolean zoomedImage = false;
    private int xCoord;
    private int yCoord;
    private int serverNumber;
    private int icePosition;

    public CorpCard(String name, String type) {
        super.setName(name);
        advancement = 0;
        isAdvanceable = false;
        trashCost = 99;
        scoreValue = 0;
        strength = 0;
        subroutines = 0;
        setType(type);
        loadImage();
    }

    public void loadImage() {
        try {
            bigImage = ImageIO.read(new File("img\\" + getName() + ".png"));
            try {
                if (isIce()) {
                    smallImage = getScaledImage(bigImage,139,100);
                } else {
                    smallImage = getScaledImage(bigImage,100,139);
                }
                
            } catch(IOException ex) {
                System.out.println("hey transform error");
            }
        } catch (IOException ex) {
            System.out.println("hey missing " + "img\\" + getName() + ".png");
        }
    }
    public void advance(int counters) {
        advancement = advancement + counters;
    }
    public void setServerNumber(int serverNumber) {
        this.serverNumber = serverNumber;
    }
    public void paintComponent (Graphics g){
        draw(g);
    }
    public void draw (Graphics g){
        //super.paintComponent(g);
        int advancementX = (xCoord+smallImage.getWidth()/2);
        int advancementY = (yCoord+smallImage.getHeight()/2);
        int tokenWidth = 40;
        int fontSize = 32;
        int fontAdjustment = 11;
        if (smallImage != null && !zoomedImage) {
            g.drawImage(smallImage, xCoord, yCoord, null);
        } else if (bigImage != null && zoomedImage) {
            advancementX = (xCoord+bigImage.getWidth()/2);
            advancementY = (yCoord+bigImage.getHeight()/2);
            tokenWidth = tokenWidth * 3;
            fontSize = fontSize * 3;
            fontAdjustment = fontAdjustment * 3;
            if (isIce()) {
                double rotationRequired = Math.toRadians (-90);
                AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, (bigImage.getWidth()), (bigImage.getHeight()));
                AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
                g.drawImage(op.filter(bigImage, null), xCoord-120, yCoord-300, null);
            } else {
                g.drawImage(bigImage, xCoord, yCoord, null);
            }
        }
        if (advancement > 0) {
            g.setColor(Color.white);
            g.fillOval(advancementX-(tokenWidth/2), advancementY-(tokenWidth/2), tokenWidth, tokenWidth);
            g.setColor(Color.black);
            g.setFont(new Font("OCR A Extended", Font.BOLD, fontSize));
            g.drawString(advancement + "", advancementX-fontAdjustment, advancementY+fontAdjustment);
            //System.out.println("hey printed adv");
        }
    }

    public static BufferedImage getScaledImage(BufferedImage image, int width, int height) throws IOException {
        int imageWidth  = image.getWidth();
        int imageHeight = image.getHeight();

        double scaleX = (double)width/imageWidth;
        double scaleY = (double)height/imageHeight;
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
        AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BICUBIC);

        return bilinearScaleOp.filter(
            image,
            new BufferedImage(width, height, image.getType()));
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
        return "Upgrade".equals(subType) || "Region".equals(subType);
    }
    public boolean isOperation() {
        return "Operation".equals(type);
    }
    public boolean isMoneyAsset() {
        return ("Money Asset".equals(subType));
    }
    public boolean isMoneyCard() {
        return ("Money Card".equals(subType) || CardAbility.getInstance().getMoneyCards().contains(name));
    }
    public boolean isCardAsset() {
        return "Card Asset".equals(subType);
    }
    public boolean isAgenda() {
        return "Agenda".equals(type);
    }
    public boolean isTrap() {
        return "Trap".equals(subType);
    }
    public boolean isTrap(String server) {
        return ("Trap".equals(subType) && attributes.contains(server));
    }
    public void advance() {
        advancement++;
    }
    public void unadvance() {
        advancement--;
    }
    public int getAdvancement() {
        return advancement;
    }
    public int getScoreValue() {
        return scoreValue;
    }
    public boolean needsIce() {
        return hasAttribute("Ice");
    }
    public boolean isAdvanceable() {
        return hasAttribute("Advanceable");
    }
    public boolean isRezzed() {
        return rezzed;
    }
    public boolean reservedForAgenda() {
        return (hasAttribute("Unique") && hasAttribute("Agenda"));
    }
    public void setTrashCost(int cost) {
        trashCost = cost;
    }
    public void setScoreValue(int value) {
        scoreValue = value;
    }
    public void setSubroutines(int value) {
        subroutines = value;
    }
    public void setStrength(int value) {
        strength = value;
    }
    public void setIcePosition(int value) {
        icePosition = value;
    }
    public void setXCoord(int value) {
        xCoord = value;
    }
    public void setYCoord(int value) {
        yCoord = value;
    }
    public void setCounters(int value) {
        counters = value;
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
    public int getServerNumber() {
        return serverNumber;
    }
    public int getTrashCost() {
        return trashCost;
    }
    public String getHostedCard() {
        return hostedCard;
    }
    public void setHostedCard(String cardName) {
        this.hostedCard = cardName;
    }
    public void rez() {
        rezzed = true;
        loadImage();
    }
    public void expose() {
        exposed = true;
        loadImage();
    }
    public void derez() {
        rezzed = false;
        loadImage();
    }
    public int getCounters() {
        return counters;
    }
    public boolean checkClickLocation(int xClick, int yClick, boolean zoom) {
        int cardHeight = (isIce()) ? 100 : 140;
        int cardWidth= (isIce()) ? 140 : 100;
        boolean isIt = xClick > (xCoord + 20) && xClick < (xCoord + cardWidth + 20) && yClick > (yCoord + 45) && yClick < (yCoord + cardHeight + 45);
        //System.out.println("hey xC " + xCoord);
        //System.out.println("hey yC " + yCoord);
        //System.out.println("hey x " + xCoord);
        //System.out.println("hey y " + yCoord);
        if (zoom) {
            if (!zoomedImage && isIt) { 
                zoomedImage = true;
            } else if (zoomedImage) {
                zoomedImage = false;
            }
        }
        return isIt;
    }
    public boolean checkClickLocation(int xClick, int yClick) {
        return checkClickLocation(xClick, yClick, false);
    }
}