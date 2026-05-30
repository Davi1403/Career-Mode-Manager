package model;

public class Player {
    String name;
    String pos;
    double value;
    int overall;
    String nationality;
    String club;
    String photo;

    public Player (String name, String pos, double value, int overall, String nationality, String club, String photo) {
        this.name = name;
        this.pos = pos;
        this.value = value;
        this.overall = overall;
        this.nationality = nationality;
        this.club = club;
        this.photo = photo;
    }

    // GETS
    public String getName(){ return name; }
    public String getPos(){ return pos; }
    public double getValue(){ return value; }
    public int getOverall(){ return overall; }
    public String getNacionalidade() { return nationality; }
    public String getClub() { return club; }
    public String getPhoto() { return photo; }
    }