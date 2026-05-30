package service;

import model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GeneticService {
    private final Map<String, List<Player>> players;
    private final int[] formation;
    private final double budget;
    private final String[] keys;
    private final Map<String, Double> posWeights;
    private final int populationSize = 50; // TP


    private final BackpackService backpack = new BackpackService();

    public GeneticService(Map<String, List<Player>> players, int[] formation, double budget, String[] keys, Map<String, Double> posWeights){
        this.players = players;
        this. formation = formation;
        this.budget = budget;
        this.keys = keys;
        this.posWeights = posWeights;
    }

    // INICIACAO RANDOMICA UNIFORME
    public List<List<Player>> firstPopulation(int populationSize){
        List<List<Player>> population = new ArrayList<>();
        for (int i = 0; i< populationSize; i++){
            population.add(backpack.genFirstSolution(players, formation, budget, keys));
        }
        return population;
    }

    public double[] fitness(List<List<Player>> population){
        int populationSize = population.size();
        double[] fits = new double[populationSize];

        for (int i=0 ; i<populationSize ; i++){
            double[] results = backpack.evaluate(population.get(i), posWeights);
            if (results[1]> budget) fits[i] = 1.0;
            else fits[i] = results[0];
        }
        return fits;
    }
    /*
    public List<Player> tournament(double[] fits){

    }*/

    public void genetic(){
        List<List<Player>> fistPopulation = firstPopulation(populationSize);
        double[] fits = fitness(fistPopulation);

        for (double fit : fits) System.out.println(fit);
    }
}


