import model.Player;
import service.Algorithms;
import service.GeneticService;
import util.PrintTable;
import util.ReadCSV;
import service.BackpackService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main() {

        ReadCSV reader = new ReadCSV();
        BackpackService bs = new BackpackService();
        Algorithms al = new Algorithms();

        // GEN ARRAYS
        String[] files = { "GK.csv", "DEF.csv", "MID.csv", "ATK.csv" };
        String[] keys = { "GK", "DEF", "MID", "ATK" };
        Map<String, List<Player>> players = reader.readFiles(files, keys);

        // TEAM CONFIG
        int budget = 100;
        // GK, DEF, MID, ATK
        int[] formation = { 1, 4, 4, 2};
        double[] pWeights = { 1.0, 1.0, 1.0, 1.0 };

        // GEN POS WEIGHTS
        Map<String, Double> posWeights = bs.genPosWeights(keys, pWeights);

        // GEN FIRST SOLUTION
        List<Player> team = bs.genFirstSolution(players, formation, budget, keys);
        double[] teamInfo = bs.evaluate(team, posWeights);

        // TESTS
        {
            PrintTable.team(team, teamInfo, "FIRST SOLUTION");

            // METHODS
            //List<Player> newTeam = al.hillClimbing(players, posWeights, team, teamInfo, budget, 11);
            //List<Player> newTeam = al.hillClimbingT(players, posWeights, team, teamInfo, budget, 11, 6);
            List<Player> newTeam = (al.simulatedAnnealing(team, teamInfo, players, posWeights, budget, 100, 0.01, 0.999));

            double[] newTeamInfo = bs.evaluate(newTeam, posWeights);
            PrintTable.team(newTeam, newTeamInfo, "BEST SOLUTION");
        }



    }
}
