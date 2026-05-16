package model;

public class Player {
    String name;
    int value;
    int overall;

    public Player(String name, int overall, int value) {
        this.name = name;
        this.overall = overall;
        this.value = value;
    }

    // GETS
    public String getName(){ return name; }
    public double getValue(){ return value; }
    public int getOverall(){ return overall; }
}