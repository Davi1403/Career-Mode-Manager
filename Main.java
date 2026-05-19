import model.Player;
import util.ReadCSV;
import service.BackpackService;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        
        ReadCSV reader = new ReadCSV();
        BackpackService bs = new BackpackService();

        List <Player> players = reader.readFile("playerDataSet2020.csv");
        int[] formation = {1, 4, 4, 2};
        int budget = 100;

        List<Player> fistSolution = bs.genFirstSolution(players, formation, budget);

        for (Player player:fistSolution){
            System.out.print(player.getName());
            System.out.print(" / " + player.getPos());
            System.out.print(" / " + player.getValue());
            System.out.print(" / " + player.getOverall());
            System.out.println();
        }

        int evaluate = bs.evaluate(fistSolution, formation, budget);
        System.out.println(evaluate);

    }
}
