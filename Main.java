import model.Player;
import util.ReadCSV;
import service.BackpackService;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        
        ReadCSV reader = new ReadCSV();
        BackpackService bs = new BackpackService();

        String[] files = {"GK.csv", "DEF.csv", "MID.csv", "ATK.csv"};
        String[] keys = {"GK", "DEF", "MID", "ATK"};

        Map<String, List<Player>> players = reader.readFiles(files, keys);
        int[] formation = {1,4, 4, 2};
        int budget = 100;

        List<Player> fistSolution = bs.genFirstSolution(players, formation, budget, keys);

        for (Player player:fistSolution){
            System.out.print(player.getName());
            System.out.print(" / " + player.getPos());
            System.out.print(" / " + player.getValue());
            System.out.print(" / " + player.getOverall());
            System.out.println();
        }

        int evaluate = bs.evaluate(fistSolution, formation, budget);
        System.out.println(evaluate);
        System.out.println(evaluate/11);

    }
}
