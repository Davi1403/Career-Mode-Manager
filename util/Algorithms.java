package util;

import model.Player;
import service.BackpackService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class Algorithms {
    Random rand = new Random();
    BackpackService bs = new BackpackService();

    public boolean neighbors(List<Player> team, double[] teamInfo, Map<String, List<Player>> players, double budget,
                           Map<String, Double> posWeights){

        int qtdSolutions = 100;

        // bestNeighbor = { overall, value }
        double[] bestNeighbor = { -1, -1 };
        Player bestOldPlayer = null;
        Player bestNewPlayer = null;
        int bestNeighborIndex = -1;

        for (int i = 0; i < qtdSolutions; i++){

            // SELECT A RANDOM PLAYER FROM TEAM
            int randomIndex = rand.nextInt(team.size()); // SELECT A RANDOM PLAYER
            Player oldPlayer = team.get(randomIndex); // SAVE OLD PLAYER
            String pos = oldPlayer.getPos(); // GET POSITION

            // SELECT A RANDOM PLAYER FROM SAME POSITION
            int randomPlayerIndex = rand.nextInt(players.get(pos).size());
            Player newPlayer = players.get(pos).get(randomPlayerIndex);

            // VALIDATE THE NEW CHOICE
            if (!newPlayer.isFlag()){
                double newValue = (teamInfo[1] - oldPlayer.getValue()) + newPlayer.getValue();
                if ( newValue <= budget){

                    // TRADE, TEMPORARILY
                    oldPlayer.setFlag(false);
                    newPlayer.setFlag(true);
                    team.set(randomIndex, newPlayer);

                    // EVALUATE NEIGHBOR
                    double[] results = bs.evaluate(team, posWeights);
                    double newOverall = results[0];

                    // COMPARE WITH THE BEST NEIGHBOR
                    if (newOverall > bestNeighbor[0]){
                        bestNeighbor[0] = newOverall;
                        bestNeighbor[1] = newValue;
                        bestOldPlayer = oldPlayer;
                        bestNewPlayer = newPlayer;
                        bestNeighborIndex = randomIndex;
                    }

                    // RESTORE ORIGINAL TEAM FOR NEW TESTS
                    newPlayer.setFlag(false);
                    oldPlayer.setFlag(true);
                    team.set(randomIndex, oldPlayer);
                }
            }
        }

        // IF GET BETTER, APPLY MODIFICATIONS
        if(bestNeighbor[0] > teamInfo[0] && bestOldPlayer != null){
            bestOldPlayer.setFlag(false);
            bestNewPlayer.setFlag(true);
            team.set(bestNeighborIndex, bestNewPlayer);

            teamInfo[0] = bestNeighbor[0];
            teamInfo[1] = bestNeighbor[1];
            return true;
        }

        return false;
    }

    public List<Player> hillClibbing(){
        List<Player> newTeam = new ArrayList<>();
        // NOT READY
        return newTeam;
    }
}
