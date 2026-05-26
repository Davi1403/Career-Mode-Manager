import model.Player;
import service.GeneticService;
import util.ReadCSV;
import service.BackpackService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        ReadCSV reader = new ReadCSV();
        BackpackService bs = new BackpackService();

        // GEN ARRAYS
        String[] files = { "GK.csv", "DEF.csv", "MID.csv", "ATK.csv" };
        String[] keys = { "GK", "DEF", "MID", "ATK" };
        Map<String, List<Player>> players = reader.readFiles(files, keys);

        // TEAM CONFIG
        int budget = 100;
        // GK, DEF, MID, ATK
        int[] formation = { 1, 4, 4, 2};
        double[] pWeights = { 1.0, 1.0, 1.0, 1.0 };

        Map<String, Double> posWeights = bs.genPosWeights(keys, pWeights);
        List<Player> fistSolution = bs.genFirstSolution(players, formation, budget, keys);

        for (Player player:fistSolution){
            System.out.print(player.getName());
            System.out.print(" / " + player.getPos());
            System.out.print(" / " + player.getValue());
            System.out.print(" / " + player.getOverall());
            System.out.println();
        }

        double[] teamInfo = bs.evaluate(fistSolution, posWeights);
        System.out.println("team overall:"+teamInfo[0]+"\nteam value:"+ teamInfo[1]);
        //System.out.println(evaluate/11);

        System.out.println();
        System.out.println("--GENETIC ALGORITHM--");
        System.out.println();

        GeneticService gn = new GeneticService(players, formation, budget, keys, posWeights);

        gn.genetic();
    }
}