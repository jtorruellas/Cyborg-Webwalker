import java.util.*;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.lang.String;
import java.text.*;
import java.awt.font.*;
import java.awt.event.*;


public class Card extends JComponent {
    protected int cost = 0;
    protected String name;
    protected String type = "";
    protected String subType = "";
    protected String attributes = "";
    protected String side = "";

    public void setCost(int cost) {
        this.cost = cost;
    }     
    public int getCost() {
        return cost;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setSide(String side) {
        this.side = side;
    }
    public String getSide() {
        return side;
    }
    public String getType() {
        return type;
    }
    public boolean isUnique(){
        return attributes.contains("Unique");
    }
    public boolean isCurrent(){
        return subType.equals("Current");
    }
    public boolean stacks(){
        return hasAttribute("Stack");
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setSubType(String subType) {
        this.subType = subType;
    }
    public String getSubType() {
        return subType;
    }
    public String getAttributes() {
        return attributes;
    }
    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }
    public boolean hasAttribute(String attribute) {
        return attributes.contains(attribute);
    }

}