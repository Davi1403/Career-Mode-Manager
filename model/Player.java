package model;

public class Player {
    String name;
    String pos;
    double value;
    int overall;

    public Player (String name, String pos, double value, int overall) {
        this.name = name;
        this.pos = pos;
        this.value = value;
        this.overall = overall;
    }

    // GETS
    public String getName(){ return name; }
    public String getPos(){ return pos; }
    public double getValue(){ return value; }
    public int getOverall(){ return overall; }
}