package util;

import model.Player;

import java.util.List;

public class PrintTable {
    public static void team(List<Player> team, double[] teamInfo, String title) {
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
        System.out.println("OVERALL: " + teamInfo[0]/11 + "\tVALUE: " + teamInfo[1]);
        System.out.println("-----------------------------------------------------------------------------------------------------------");
    }
}
