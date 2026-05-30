import model.Player;
import service.Algorithms;
import service.GeneticService;
import util.PrintTable;
import util.ReadCSV;
import service.BackpackService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import service.GeneticService.AGResults;

public class Main {

    public static void main(String[] args) {

        ReadCSV reader = new ReadCSV();
        BackpackService bs = new BackpackService();
        Algorithms al = new Algorithms();

        // GEN ARRAYS
        String[] files = { "GK.csv", "DEF.csv", "MID.csv", "ATK.csv" };
        String[] keys = { "GK", "DEF", "MID", "ATK" };
        Map<String, List<Player>> players = reader.readFiles(files, keys);

        // TEAM CONFIG
        int budget = 1000;
        int[] formation = { 1, 4, 4, 2}; // GK, DEF, MID, ATK
        double[] pWeights = { 1.0, 1.0, 1.0, 1.0 }; // GK, DEF, MID, ATK

        // GEN POS WEIGHTS
        Map<String, Double> posWeights = bs.genPosWeights(keys, pWeights);




        double gainHC = 0.0;
        double overAllHC = 0.0;

        double gainHCT = 0.0;
        double overAllHCT = 0.0;

        double gainSA = 0.0;
        double overAllSA = 0.0;

        /*
        for (int i=0 ; i<20 ; i++){

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
            gainHC += (100* Math.abs(teamInfo[0] - resultHC[0])) / teamInfo[0];
            overAllHC+= resultHC[0];

            double[] infoHCT = {teamInfo[0], teamInfo[1]};
            List<Player> teamHCT = al.hillClimbingT(players, posWeights, team, infoHCT, budget, 11, 6);
            double[] resultHCT = bs.evaluate(teamHCT, posWeights);
            PrintTable.team(teamHCT, resultHCT, "HILL CT SOLUTION");
            gainHCT += (100* Math.abs(teamInfo[0] - resultHCT[0])) / teamInfo[0];
            overAllHCT+= resultHCT[0];

            double[] infoSA = {teamInfo[0], teamInfo[1]};
            List<Player> teamSA = al.simulatedAnnealing(team, infoSA, players, posWeights, budget, 500, 0.01, 0.9);
            double[] resultSA = bs.evaluate(teamSA, posWeights);
            PrintTable.team(teamSA, resultSA, "ANNEALING SOLUTION");
            gainSA += (100* Math.abs(teamInfo[0] - resultSA[0])) / teamInfo[0];
            overAllSA+= resultSA[0];

        }
        System.out.println("MEDIA DE GANHO HC = " + gainHC/20);
        System.out.println("MEDIA DE OVERHALL HC = " + overAllHC/20/11);

        System.out.println("MEDIA DE GANHO HCT = " + gainHCT/20);
        System.out.println("MEDIA DE OVERHALL HCT = " + overAllHCT/20/11);

        System.out.println("MEDIA DE GANHO ANNELING = " + gainSA/20);
        System.out.println("MEDIA DE OVERHALL ANNELING = " + overAllSA/20/11);

        */



        System.out.println("\n--GENETIC ALGORITHM--\n");


        GeneticService gn = new GeneticService(players, formation, budget, keys, posWeights);

        double[] gain = new double[6];
        double[] overAllSum = new double[6];
        for (int i=0 ; i<20 ; i++){
            AGResults results = gn.genetic(20, 50, 0.8, 0.0, 0.0);
            gain[0] += (100* Math.abs(results.initialOverall[0] - results.finalOverall[0])) / results.initialOverall[0];
            overAllSum[0] += results.finalOverall[0];

            results = gn.genetic(20, 50, 0.8, 0.1, 0.1);
            gain[1] += (100* Math.abs(results.initialOverall[0] - results.finalOverall[0])) / results.initialOverall[0];
            overAllSum[1] += results.finalOverall[0];

            results = gn.genetic(50, 20, 0.8, 0.1, 0.1);
            gain[2] += (100* Math.abs(results.initialOverall[0] - results.finalOverall[0])) / results.initialOverall[0];
            overAllSum[2] += results.finalOverall[0];

            results = gn.genetic(50, 20, 0.8, 0.8, 0.1);
            gain[3] += (100* Math.abs(results.initialOverall[0] - results.finalOverall[0])) / results.initialOverall[0];
            overAllSum[3] += results.finalOverall[0];

            results = gn.genetic(200, 20, 0.8, 0.1, 0.1);
            gain[4] += (100* Math.abs(results.initialOverall[0] - results.finalOverall[0])) / results.initialOverall[0];
            overAllSum[4] += results.finalOverall[0];

            results = gn.genetic(50, 200, 0.8, 0.1, 0.1);
            gain[5] += (100* Math.abs(results.initialOverall[0] - results.finalOverall[0])) / results.initialOverall[0];
            overAllSum[5] += results.finalOverall[0];

        }

        for (int i=0 ; i<6 ; i++){
            System.out.println("GAIN " + (i+1) + " = " + gain[i]/20);
            System.out.println("OVERALL " + (i+1) + " = " + overAllSum[i]/20/11);
            System.out.println();
        }


    }
}
