package model;

public class Player {
    boolean flag; 
    String name;
    String pos;
    double value;
    int overall;

    public Player (String name, String pos, double value, int overall) {
        this.flag = false;
        this.name = name;
        this.pos = pos;
        this.value = value;
        this.overall = overall;
    }

    // GETS
    public boolean isFlag(){return flag;}
    public String getName(){ return name; }
    public String getPos(){ return pos; }
    public double getValue(){ return value; }
    public int getOverall(){ return overall; }
    
    // SETS
    public void setFlag(boolean flag){
        this.flag = flag;
    }
}