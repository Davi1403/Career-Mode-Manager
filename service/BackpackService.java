package service;

import model.Player;

import java.util.*;

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


    public Map<String, Double> genPosWeights(String[] keys, double[] pWeights){
        if (keys.length != pWeights.length) {
            throw new IllegalArgumentException("Erro: A quantidade de chaves e pesos deve ser idêntica!");
        }

        Map<String,Double> posWeights = new HashMap<>();
        for(int i = 0; i < keys.length; i++) {
            posWeights.put(keys[i], pWeights[i]);
        }

        return posWeights;
    }


    // EVALUATE THE OVERALL
    public double[] evaluate(List<Player> team, Map<String, Double> posWeights){

        int minOverall = 999;
        int maxOverall = -1;
        double baseOverall = 0;
        double teamWorkBonus = 0;
        double totalValue = 0;
        Map<String, Integer> nationCount = new HashMap<>();
        Map<String, Integer> clubCount = new HashMap<>();

        for (Player player:team){

            // GET MIN AND MAX OVERALL
            if(player.getOverall() > maxOverall) maxOverall = player.getOverall();
            if(player.getOverall() < minOverall) minOverall = player.getOverall();

            // COUNT NATIONALITY AND CLUB
            nationCount.put(player.getNat(), nationCount.getOrDefault(player.getNat(),0) + 1);
            clubCount.put(player.getClub(), clubCount.getOrDefault(player.getClub(),0) + 1);

            // APPLY POSITION WEIGHT
            double playerOverall = player.getOverall() * posWeights.get(player.getPos());

            // SUM OVERALL AND VALUE
            baseOverall += playerOverall;
            totalValue += player.getValue();
        }

        // NATIONALITY BONUS COUNT
        for(int i : nationCount.values()){
            if(i > 1) teamWorkBonus += (i-1) * 2;
        }

        // CLUB BONUS COUNT
        for(int i : clubCount.values()){
            if(i > 1) teamWorkBonus += (i-1) * 3;
        }

        // APPLIES BONUS AND PENALTIES, RETURN TOTALOVERALL AND TOTALVALUE
        return new double[] {(baseOverall + teamWorkBonus) - (maxOverall - minOverall), totalValue};
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

}
