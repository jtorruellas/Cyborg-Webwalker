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

public class RunnerCard extends Card {

    private int strength = 0;
    private int virusCounters = 0;
    private boolean rezzed = false;
    public int armitageCreds = 12;
    private BufferedImage bigImage;
    private BufferedImage smallImage;
    private boolean zoomedImage = false;
    private int xCoord;
    private int yCoord;

    public RunnerCard(String name) {
        super.setName(name);
        loadImage();
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
    public void loadImage() {
        try {
            System.out.println("hey loadImage " + "img\\" + getName() + ".png");
            bigImage = ImageIO.read(new File("img\\" + getName() + ".png"));
            smallImage = getScaledImage(bigImage,150,209);
        } catch (IOException ex) {
            System.out.println("hey missing " + "img\\" + getName() + ".png");
        }
    }
    public void paintComponent (Graphics g){
        draw(g);
    }
    public void draw (Graphics g){
        //super.paintComponent(g);
        if (smallImage != null && !zoomedImage) {
            g.drawImage(smallImage, xCoord, yCoord, null);
        } else if (bigImage != null && zoomedImage) {
            g.drawImage(bigImage, xCoord, yCoord-209, null);
        }
        if (virusCounters > 0) {
            g.setColor(Color.red);
            g.fillOval((xCoord+smallImage.getWidth()/2)-20, (yCoord+smallImage.getHeight()/2)-20, 40, 40);
            g.setColor(Color.black);
            g.setFont(new Font("OCR A Extended", Font.BOLD, 32));
            g.drawString(virusCounters + "", xCoord+(smallImage.getWidth()/2)-11, yCoord+(smallImage.getHeight()/2)+11);
            //System.out.println("hey printed adv");
        }
    }
    public void setXCoord(int value) {
        xCoord = value;
    }
    public void setYCoord(int value) {
        yCoord = value;
    }
    public static BufferedImage getScaledImage(BufferedImage image, int width, int height) throws IOException {
        int imageWidth  = image.getWidth();
        int imageHeight = image.getHeight();

        double scaleX = (double)width/imageWidth;
        double scaleY = (double)height/imageHeight;
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
        AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);

        return bilinearScaleOp.filter(
            image,
            new BufferedImage(width, height, image.getType()));
    }
    public boolean checkClickLocation(int xClick, int yClick, boolean zoom) {
        int cardHeight = 209;
        int cardWidth= 150;
        boolean isIt = xClick > (xCoord + 20) && xClick < (xCoord + cardWidth + 20) && yClick > (yCoord + 30) && yClick < (yCoord + cardHeight + 30);
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