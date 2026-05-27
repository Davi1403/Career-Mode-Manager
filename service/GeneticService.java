package service;

import model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import util.PrintTable;
public class GeneticService {
    private final Map<String, List<Player>> players;
    private final int[] formation;
    private final int budget;
    private final String[] keys;
    private final Map<String, Double> posWeights;
    private final int populationSize = 2; // TP


    private final BackpackService backpack = new BackpackService();

    public GeneticService(Map<String, List<Player>> players, int[] formation, int budget, String[] keys, Map<String, Double> posWeights){
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
<<<<<<< HEAD
    /*
    public List<Player> tournament(double[] fits){
=======

    public List<Player> tournament(double[] fits, List<List<Player>> population){
        int tp = population.size();

        Random random = new Random();
        int team1 = random.nextInt(tp);
        int team2 = random.nextInt(tp);
        if (fits[team1] > fits[team2]) return population.get(team1);
        else return population.get(team2);
>>>>>>> 7824990 (feat: tournament)

    }*/

    public void genetic(){
        List<List<Player>> fistPopulation = firstPopulation(populationSize);
        double[] fits = fitness(fistPopulation);
        PrintTable p = new PrintTable();
        p.printTeamTable(fistPopulation.get(0), "Team 1");
        double[] results = backpack.evaluate(fistPopulation.get(0), posWeights);
        System.out.println(results[0]);
        p.printTeamTable(fistPopulation.get(1), "Team 2");
        results = backpack.evaluate(fistPopulation.get(1), posWeights);
        System.out.println(results[0]);

        List<Player> team = tournament(fits, fistPopulation);
        p.printTeamTable(team, "TIME VENCEDOR");
    }
}


