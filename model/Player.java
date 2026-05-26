package model;

public class Player {
    boolean flag;
    String name;
    String pos;
    double value;
    int overall;
    String nationality;
    String club;

    public Player (String name, String pos, double value, int overall, String nationality, String club) {
        this.flag = false;
        this.name = name;
        this.pos = pos;
        this.value = value;
        this.overall = overall;
        this.nationality = nationality;
        this.club = club;
    }

    // GETS
    public boolean isFlag(){ return flag; }
    public String getName(){ return name; }
    public String getPos(){ return pos; }
    public double getValue(){ return value; }
    public int getOverall(){ return overall; }
    public String getNat() { return nationality; }
    public String getClub() { return club; }

    // SETS
    public void setFlag(boolean flag){
        this.flag = flag;
    }
}