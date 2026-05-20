package service;

import model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BackpackService {
    Random rand = new Random();

    public int getIndexPosition(String pos){
        switch (pos) {
            case "GK": return 0;
            case "DEF": return 1;
            case "MID": return 2;
            case "ATK": return 3;
            default: return -1;
        }
    }

    public Player draft(Map<String, List<Player>> players, String pos){
        int sortedIndex = rand.nextInt(players.get(pos).size());
        return players.get(pos).get(sortedIndex);
    }


    // GENERATE FIRST SOLUTION
    public List<Player> genFirstSolution(Map<String, List<Player>> players, int[] formation, int budget, String[] keys){
        List<Player> team = new ArrayList<>();
        double value = 0;
        int attempts = 0;

        // DRAFT PLAYERS
        for(int i = 0; i < formation.length; i++) {
            int j=0;
            while (j < formation[i]) {

                Player draftedPlayer = draft(players, keys[i]);
                if(!draftedPlayer.isFlag()){
                    double aux = value + draftedPlayer.getValue();
                    if (aux <= budget) {
                        draftedPlayer.setFlag(true);
                        team.add(draftedPlayer);
                        value = aux;
                        j++;
                        attempts = 0;
                    }
                }

                attempts++;
                if(attempts>4000){
                    System.out.println("ERROR ON GENFIRSTSOLUTION");
                    return team;
                }
            }
        }
        return team;
    }

    // EVALUATE THE OVERALL
    public int evaluate(List<Player> team, int[] formation, int budget){
        int totalOverall = 0;
        double totalValue = 0;

        if (team.size() != 11){
            System.out.println("INCOMPLETE TEAM");
            return 0;
        }


        for (Player player:team){

            //CHECK THE FORMATION
            int indexPosition = getIndexPosition(player.getPos());
            if (formation[indexPosition] == 0){
                System.out.println("WRONG FORMATION");
                return 0;
            }
            formation[indexPosition] --;

            // SUM OVERALL WITH BUDGET LIMIT
            totalOverall += player.getOverall();
            totalValue += player.getValue();

            if (totalValue > budget) {
                System.out.println("BLEW THE BUDGET");
                return 0;
            }
        }
        return totalOverall;
    }


}
