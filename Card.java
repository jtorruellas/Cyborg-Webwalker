import java.util.*;

public class Card {
    protected int cost = 0;
    protected String name;
    protected String type = "";
    protected String subType = "";
    protected String attributes = "";

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
    public boolean activate(){
        return true;
    }
    public String getType() {
        return type;
    }
    public boolean isUnique(){
        return attributes.contains("Unique");
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
    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

}