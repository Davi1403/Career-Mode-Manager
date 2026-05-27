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
        int budget = 200;
        int[] formation = { 1, 4, 4, 2}; // GK, DEF, MID, ATK
        double[] pWeights = { 1.0, 1.0, 1.0, 1.0 }; // GK, DEF, MID, ATK

        // GEN POS WEIGHTS
        Map<String, Double> posWeights = bs.genPosWeights(keys, pWeights);

        // GEN FIRST SOLUTION
        List<Player> team = bs.genFirstSolution(players, formation, budget, keys);
        double[] teamInfo = bs.evaluate(team, posWeights);

        // TESTS
        PrintTable.team(team, teamInfo, "FIRST SOLUTION");

        // METHODS
        double[] infoHC = {teamInfo[0], teamInfo[1]};
        List<Player> teamHC = al.hillClimbing(players, posWeights, team, infoHC, budget, 11);
        double[] resultHC = bs.evaluate(teamHC, posWeights);
        PrintTable.team(teamHC, resultHC, "HILL C SOLUTION");

        double[] infoHCT = {teamInfo[0], teamInfo[1]};
        List<Player> teamHCT = al.hillClimbingT(players, posWeights, team, infoHCT, budget, 11, 6);
        double[] resultHCT = bs.evaluate(teamHCT, posWeights);
        PrintTable.team(teamHCT, resultHCT, "HILL CT SOLUTION");

        double[] infoSA = {teamInfo[0], teamInfo[1]};
        List<Player> teamSA = al.simulatedAnnealing(team, infoSA, players, posWeights, budget, 100, 0.01, 0.99);
        double[] resultSA = bs.evaluate(teamSA, posWeights);
        PrintTable.team(teamSA, resultSA, "ANNEALING SOLUTION");

        //System.out.println("\n--GENETIC ALGORITHM--\n");
        //GeneticService gn = new GeneticService(players, formation, budget, keys, posWeights);
        //gn.genetic();
    }
}
