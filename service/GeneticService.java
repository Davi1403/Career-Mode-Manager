package service;

import model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GeneticService {
    private final Map<String, List<Player>> players;
    private final int[] formation;
    private final int budget;
    private final String[] keys;

    private final BackpackService backpack = new BackpackService();

    public GeneticService(Map<String, List<Player>> players, int[] formation, int budget, String[] keys){
        this.players = players;
        this. formation = formation;
        this.budget = budget;
        this.keys = keys;
    }

    // INICIACAO RANDOMICA UNIFORME
    public List<List<Player>> firstPopulation(int populationSize){
        List<List<Player>> population = new ArrayList<>();
        for (int i = 0; i< populationSize; i++){
            population.add(backpack.genFirstSolution(players, formation, budget, keys));
        }
        return population;
    }

    
}
