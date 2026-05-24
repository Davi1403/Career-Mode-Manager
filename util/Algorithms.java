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


    public void hillClimbing(List<Player> firstSolution, double[] teamInfo, Map<String, List<Player>> players,
                             double budget, Map<String, Double> posWeights){
        int steps=0;
        boolean running = true;
        while(running){
            steps++;
            running = neighbors(firstSolution, teamInfo, players, budget, posWeights);
        }
        System.out.println(steps);
    }


    public void hillClimbingT(List<Player> firstSolution, double[] teamInfo, Map<String, List<Player>> players,
                             double budget, Map<String, Double> posWeights, int tMax){

        int steps=0; int count=0; boolean result;
        do {
            result = neighbors(firstSolution, teamInfo, players, budget, posWeights);
            if(result) steps = 0;
            else steps++;
            count++;
        } while (steps < tMax);
        System.out.println(count);
    }


    public double[] neighborsAnnealing(List<Player> auxTeam, Map<String, List<Player>> players, Map<String, Double> posWeights,
                                       double currentValue, double budget){

        // SELECT A RANDOM PLAYER FROM TEAM
        int oldPlayerIndex = rand.nextInt(auxTeam.size()); // SELECT A RANDOM PLAYER
        Player oldPlayer = auxTeam.get(oldPlayerIndex); // SAVE OLD PLAYER
        String pos = oldPlayer.getPos(); // GET POSITION

        Player newPlayer;
        int newPlayerIndex;
        do {
            // SELECT A VALID RANDOM PLAYER FROM THE SAME POSITION
            newPlayerIndex = rand.nextInt(players.get(pos).size());
            newPlayer = players.get(pos).get(newPlayerIndex);
        } while (newPlayer.isFlag() || (((currentValue - oldPlayer.getValue()) + newPlayer.getValue()) > budget));

        auxTeam.set(oldPlayerIndex, newPlayer);
        double[] results = bs.evaluate(auxTeam, posWeights);
        return new double[] {results[0], results[1], oldPlayerIndex, newPlayerIndex};
    }

    public void updateTeam(Player oldPlayer, Player newPlayer, double[] teamInfo, List<Player> team, double oldPlayerIndex,
                           double newOverall, double newValue){
        // FLAGS UPDATE
        oldPlayer.setFlag(false);
        newPlayer.setFlag(true);

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
        // BestTeamInfo = {overall, value}
        double[] bestTeamInfo = {teamInfo[0],teamInfo[1]};

        while(currentTemp > finalTemp){
            List<Player> auxTeam = new ArrayList<>(team);
            // results = {overall, value, oldPlayerIndex, newPlayerIndex}
            double[] results = neighborsAnnealing(auxTeam, players, posWeights, teamInfo[1], budget);
            Player oldPlayer = team.get((int) results[2]);
            Player newPlayer = players.get(oldPlayer.getPos()).get((int) results[3]);

            // NEW OVERALL > CURRENT OVERALL
            if(results[0] > teamInfo[0]){
                updateTeam(oldPlayer, newPlayer, teamInfo, team, results[2], results[0], results[1]);

                // BEST TEAM UPDATE
                if (teamInfo[0] > bestTeamInfo[0]){
                    bestTeam = new ArrayList<>(team);
                    bestTeamInfo[0] = teamInfo[0];
                    bestTeamInfo[1] = teamInfo[1];
                }
            }else{
                double D = teamInfo[0] - results[0];
                double r = rand.nextDouble();
                double aux = Math.exp(-D/currentTemp);

                if (r < aux){
                    updateTeam(oldPlayer, newPlayer, teamInfo, team, results[2], results[0], results[1]);
                }
            }
            currentTemp = currentTemp* FR;
        }
        for(int i = 0; i < team.size(); i++){
            team.get(i).setFlag(false);
            bestTeam.get(i).setFlag(true);
        }
        return bestTeam;
    }

}
