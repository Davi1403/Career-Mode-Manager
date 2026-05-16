import model.Player;
import util.ReadCSV;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        ReadCSV reader = new ReadCSV();
        List <Player> players = reader.readFile("playerDataSheet.csv");

        for (Player player:players){
            System.out.print(player.getName());
            System.out.print(" / " + player.getOverall());
            System.out.print(" / " + player.getValue());
            System.out.println();
        }
    }
}
