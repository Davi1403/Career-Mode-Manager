public class Player {
    String name;
    double value;
    int overall;

    public Player(String name, double value, int overall) {
        String this.name = name;
        double this.value = value;
        int this.overall = overall;
    }

    // GETS
    public String getName(){ return name; }
    public double getValue(){ return value; }
    public int getOverall(){ return overall; }
}