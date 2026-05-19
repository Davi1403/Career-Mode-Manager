package service;

import model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BackpackService {

    public int getIndexPosition(String pos){
        switch (pos) {
            case "GK": return 0;
            case "DEF": return 1;
            case "MID": return 2;
            case "ATK": return 3;
            default: return -1;
        }
    }

    // GENERATE FIRST SOLUTION
    Random rand = new Random();
    public List<Player> genFirstSolution(List<Player> players, int[] formation, int budget){
        
        int value = 0;
        int draftedPlayers = 0;
        int i=0;
        List<Player> team = new ArrayList<>();
        
        while (draftedPlayers < 11){
            int sortedIndex = rand.nextInt(players.size());
            Player sortedPlayer = players.get(sortedIndex);
            
            if (!team.contains(sortedPlayer)){
                if(value + sortedPlayer.getValue() <= budget){
                    int index = getIndexPosition(sortedPlayer.getPos());
                    if(formation[index] != 0){
                        team.add(sortedPlayer);
                        formation[index]--;
                        draftedPlayers++;
                    }
                }
            }
            i++;
            if(i>10000) return new ArrayList<>();
        }
        return team;
    }

    //public List<Player> team = new ArrayList<>();
    //while (team.size()) <= 11){

    //}

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
