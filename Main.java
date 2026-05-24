import model.Player;
import util.Algorithms;
import util.ReadCSV;
import service.BackpackService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class Main {

    public static void printTeamTable(List<Player> team, String title) {
        System.out.println("\n=== " + title.toUpperCase() + " ===");
        System.out.println("-----------------------------------------------------------------------------------------------------------");

        // Aumentei o espaço do Valor para caber a formatação nova
        System.out.printf("%-25s | %-4s | %-7s | %-15s | %-15s | %-20s%n",
                "NOME", "POS", "OVERALL", "VALOR", "NACIONALIDADE", "CLUBE");

        System.out.println("-----------------------------------------------------------------------------------------------------------");

        String[] tacticalOrder = {"GK", "DEF", "MID", "ATK"};

        for (String targetPos : tacticalOrder) {
            for (Player player : team) {
                if (player.getPos().equals(targetPos)) {

                    // Formata o dinheiro bonitinho antes de imprimir
                    String valorFormatado = String.format("€ %.2f M", player.getValue());

                    System.out.printf("%-25s | %-4s | %-7d | %-15s | %-15s | %-20s%n",
                            player.getName(),
                            player.getPos(),
                            player.getOverall(),
                            valorFormatado,
                            player.getNat(),
                            player.getClub());
                }
            }
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------");
    }

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
        int[] formation = { 1, 4, 4, 2}; // GK, DEF, MID, ATK
        double[] pWeights = { 1.0, 1.0, 1.0, 1.0 }; // GK, DEF, MID, ATK

        Map<String, Double> posWeights = bs.genPosWeights(keys, pWeights);
        List<Player> team = bs.genFirstSolution(players, formation, budget, keys);
        double[] teamInfo = bs.evaluate(team, posWeights);

        printTeamTable(team,"TIME INICIAL");
        System.out.println("OVERALL: "+teamInfo[0]/11+"\tCUSTO($): "+ teamInfo[1]);

        //al.hillClimbing(team, teamInfo, players, budget, posWeights);
        //al.hillClimbingT(team, teamInfo, players, budget, posWeights, 1000);
        team = new ArrayList<>(al.simulatedAnnealing(team, teamInfo, players, posWeights, budget, 100, 0.01, 0.9999));

        teamInfo = bs.evaluate(team, posWeights);
        printTeamTable(team,"TIME OTIMIZADO");
        System.out.println("OVERALL: "+teamInfo[0]/11+"\tCUSTO($): "+ teamInfo[1]);
    }
}
