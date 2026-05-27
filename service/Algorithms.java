package service;

import model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class Algorithms {
    Random rand = new Random();
    BackpackService bs = new BackpackService();

    public double[] neighbors(Map<String, List<Player>> players, Map<String, Double> posWeights,
                              List<Player> auxTeam, double currentValue, double budget, int qtdSolutions){

        double[] bestResult = {0,0,0,0};
        for(int i = 0; i < qtdSolutions; i++){
            // SELECT A RANDOM PLAYER FROM TEAM
            int oldPlayerIndex = rand.nextInt(auxTeam.size()); // SELECT A RANDOM PLAYER
            Player oldPlayer = auxTeam.get(oldPlayerIndex); // SAVE OLD PLAYER
            String pos = oldPlayer.getPos(); // GET OLD PLAYER POSITION

            // SELECT A VALID NEW RANDOM PLAYER
            Player newPlayer;
            int newPlayerIndex;
            double auxValue = currentValue - oldPlayer.getValue();
            do {
                newPlayerIndex = rand.nextInt(players.get(pos).size());
                newPlayer = players.get(pos).get(newPlayerIndex);
                auxValue += newPlayer.getValue();
            } while (auxTeam.contains(newPlayer) || auxValue > budget);

            // TRADE PLAYERS FOR EVALUATE
            auxTeam.set(oldPlayerIndex, newPlayer);
            // RUN EVALUATE AND SAVE THE RESULTS
            double[] results = bs.evaluate(auxTeam, posWeights);
            // UNDO THE TRADE
            auxTeam.set(oldPlayerIndex, oldPlayer);

            // SAVE THE BEST SOLUTION
            if(results[0] > bestResult[0]) {
                bestResult[0] = results[0];
                bestResult[1] = results[1];
                bestResult[2] = oldPlayerIndex;
                bestResult[3] = newPlayerIndex;
            }
        }
        return bestResult;
    }


    public List<Player> hillClimbing(Map<String, List<Player>> players, Map<String, Double> posWeights,
                                        List<Player> team, double[] teamInfo, double budget, int qtdSolutions){

        double[] results;
        List<Player> auxTeam = new ArrayList<>(team);

        int t=0; int counter=0;
        do {
            results = neighbors(players, posWeights, auxTeam, teamInfo[1], budget, qtdSolutions);

            // OVERALL IS BETTER?
            if(results[0] > teamInfo[0]){
                // TRADE PLAYERS
                int oldPlayerIndex = (int) results[2];
                int newPlayerIndex = (int) results[3];
                auxTeam.set(oldPlayerIndex, players.get(auxTeam.get(oldPlayerIndex).getPos()).get(newPlayerIndex));
                // TRADE TEAM INFOS
                teamInfo[0] = results[0];
                teamInfo[1] = results[1];
            }
            else t++;
            counter++;
        } while (t < 1);

        System.out.println(counter);
        return auxTeam;
    }


    public List<Player> hillClimbingT(Map<String, List<Player>> players, Map<String, Double> posWeights,
                                     List<Player> team, double[] teamInfo, double budget, int qtdSolutions, int tMax){

        double[] results;
        List<Player> auxTeam = new ArrayList<>(team);

        int t=0; int counter=0;
        do {
            results = neighbors(players, posWeights, auxTeam, teamInfo[1], budget, qtdSolutions);

            // OVERALL IS BETTER?
            if(results[0] > teamInfo[0]){
                // TRADE PLAYERS
                int oldPlayerIndex = (int) results[2];
                int newPlayerIndex = (int) results[3];
                auxTeam.set(oldPlayerIndex, players.get(auxTeam.get(oldPlayerIndex).getPos()).get(newPlayerIndex));
                // TRADE TEAM INFOS
                teamInfo[0] = results[0];
                teamInfo[1] = results[1];
                // RESET t
                t=0;
            }
            else t++;
            counter++;
        } while (t < tMax);

        System.out.println(counter);
        return auxTeam;
    }


    public void updateTeam(Player newPlayer, double[] teamInfo, List<Player> team, double oldPlayerIndex,
                           double newOverall, double newValue){
        // TEAM INFO UPDATE
        teamInfo[0] = newOverall;
        teamInfo[1] = newValue;

        // TEAM UPDATE
        team.set((int) oldPlayerIndex, newPlayer);
    }

    public List<Player> simulatedAnnealing(List<Player> team, double[] teamInfo, Map<String, List<Player>> players, Map<String, Double> posWeights,
                                           double budget, double initialTemp, double finalTemp, double FR){

        double currentTemp = initialTemp;
        List<Player> bestTeam = new ArrayList<>(team);
        //                          {overall, value}
        double[] bestTeamInfo = {teamInfo[0],teamInfo[1]};

        while(currentTemp > finalTemp){
            List<Player> auxTeam = new ArrayList<>(team);
            // results = {overall, value, oldPlayerIndex, newPlayerIndex}
            double[] results = neighbors(players, posWeights, auxTeam, teamInfo[1], budget, 1);
            Player oldPlayer = team.get((int) results[2]);
            Player newPlayer = players.get(oldPlayer.getPos()).get((int) results[3]);

            // NEW OVERALL > CURRENT OVERALL
            if(results[0] > teamInfo[0]){
                updateTeam(newPlayer, teamInfo, team, results[2], results[0], results[1]);

                // BEST TEAM UPDATE
                if (teamInfo[0] > bestTeamInfo[0]){
                    bestTeam = new ArrayList<>(team);
                    bestTeamInfo[0] = teamInfo[0];
                    bestTeamInfo[1] = teamInfo[1];
                }
            }
            else {
                double D = teamInfo[0] - results[0];
                double r = rand.nextDouble();
                double aux = Math.exp(-D/currentTemp);
                if (r < aux) updateTeam(newPlayer, teamInfo, team, results[2], results[0], results[1]);
            }
            currentTemp = currentTemp* FR;
        }
        return bestTeam;
    }

}
