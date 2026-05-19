package service;

import model.Player;

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
    public void genFirstSolution(List<Player> players, int[] formation, int budget){
        
        int value = 0;
        int draftedPlayers = 0;
        int i=0;
        
        while (draftedPlayers < 11){
            int sortedIndex = rand.nextInt(players.size());
            Player sortedPlayer = players.get(sortedIndex);
            
            if (!sortedPlayer.isFlag()){
                if(value + sortedPlayer.getValue() <= budget){
                    int index = getIndexPosition(sortedPlayer.getPos());
                    if(formation[index] != 0){
                        sortedPlayer.setFlag(true);
                        formation[index]--;
                        draftedPlayers++;
                    }
                }
            }
            i++;
            if(i>10000) break;
        }
    }

    //public List<Player> team = new ArrayList<>();
    //while (team.size()) <= 11){

    //}
}
