package service;

import model.Player;
import org.w3c.dom.ls.LSInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static util.PrintTable.team;

public class GeneticService {
    private final Map<String, List<Player>> players;
    private final int[] formation;
    private final double budget;
    private final String[] keys;
    private final Map<String, Double> posWeights;
    private int populationSize; // TP
    private int gerationNumber; //NG
    private double growthRate; //TC
    private double mutationRate; //TM
    private double generationInterval;  //IG


    private final BackpackService backpack = new BackpackService();
    private final Random random = new Random();

    public GeneticService(Map<String, List<Player>> players, int[] formation, double budget, String[] keys, Map<String, Double> posWeights){
        this.players = players;
        this. formation = formation;
        this.budget = budget;
        this.keys = keys;
        this.posWeights = posWeights;



    }

    public class Individual {
        List<Player> team;
        double fit;

        Individual(List<Player> team, double fit){
            this.team = team;
            this.fit = fit;
        }
    }

    public class AGResults{
        public List<Player> initialSolution;
        public List<Player> finalSolution;
        public double[] initialOverall;
        public double[] finalOverall;

        public AGResults(List<Player> initialSolution, List<Player> finalSolution, double[] initialOverall, double[] finalOverall){
            this.initialSolution = initialSolution;
            this.finalSolution = finalSolution;
            this.initialOverall = initialOverall;
            this.finalOverall = finalOverall;
        }
    }

    public List<List<Player>> order (List<List<Player>> population, double[] fits){
        List<Individual> individual= new ArrayList<>();

        for (int i=0 ; i<population.size() ; i++){
            individual.add(new Individual(population.get(i), fits[i]));
        }

        individual.sort((a, b) -> Double.compare(b.fit, a.fit));


        List<List<Player>> orderedTeam = new ArrayList<>();

        for(Individual i : individual){
            orderedTeam.add(i.team);

        }
        return orderedTeam;
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


    public List<Player> tournament(double[] fits, List<List<Player>> population){
        int tp = population.size();


        int team1 = random.nextInt(tp);
        int team2 = random.nextInt(tp);
        if (fits[team1] > fits[team2]) return population.get(team1);
        else return population.get(team2);
    }

    public List<Player> mutation(List<Player> team){
        int randomIndex = random.nextInt(11);
        Player randomTeamPlayer = team.get(randomIndex);
        //System.out.println("PLAYER OUT = " + randomTeamPlayer.getName() + randomTeamPlayer.getPos());
        Player randomPlayer = players.get(randomTeamPlayer.getPos()).get(random.nextInt(players.get(randomTeamPlayer.getPos()).size())) ;
        //System.out.println("PLAYER IN = " + randomPlayer.getName() + randomPlayer.getPos());
        if (!team.contains(randomPlayer))team.set(randomIndex, randomPlayer);
        return team;
    }

    public List<List<Player>> crossing (List<Player> father1, List<Player> father2){
        int cut1 = formation[1] + 1; // GK + DEF
        int cut2 = formation[2] + cut1; // MID

        List<Player> son1 = new ArrayList<>(father1.subList(0, cut1));
        son1.addAll(father2.subList(cut1, cut2));
        son1.addAll(father1.subList(cut2, 11));

        List<Player> son2 = new ArrayList<>(father2.subList(0, cut1));
        son2.addAll(father1.subList(cut1, cut2));
        son2.addAll(father2.subList(cut2, 11));

        List<List<Player>> children = new ArrayList<>();
        children.add(son1);
        children.add(son2);

        return children;
    }

    public List<List<Player>> descendants(double[] fits, List<List<Player>> population){
        int i = 0;
        List<List<Player>> descendants = new ArrayList<>();


        int descendantsNumber = populationSize*2;
        while (i < descendantsNumber){
            List<Player> f1 = tournament(fits, population);
            List<Player> f2 = tournament(fits, population);

            if (random.nextDouble(0,1) <= growthRate){
                descendants.addAll(crossing(f1,f2));
            }else{
                descendants.add(new ArrayList<>(f1));
                descendants.add(new ArrayList<>(f2));
            }

            if (random.nextDouble(0,1) <= mutationRate){
                descendants.set(i, mutation(descendants.get(i)));
            }
            if (random.nextDouble(0,1) <= mutationRate){
                descendants.set(i+1, mutation(descendants.get(i+1)));
            }
            i+=2;
        }
        return descendants;
    }

    public List<List<Player>> newPop(List<List<Player>> orderedPopulation, List<List<Player>> descendants){
        List<List<Player>> nextGen = new ArrayList<>();
        int elite =  (int) Math.ceil(generationInterval*populationSize);
        for (int i=0 ; i<elite ; i++){

            nextGen.add(new ArrayList<>(orderedPopulation.get(i)));
        }

        for (int j=0 ; j<populationSize-elite ; j++){
            nextGen.add(new ArrayList<>(descendants.get(j)));
        }
        return nextGen;
    }



    public AGResults genetic(int populationSize, int gerationNumber, double growthRate, double mutationRate, double generationInterval,  List<List<Player>> pop, double[] fits){
        this.populationSize = populationSize; // TP
        this.gerationNumber = gerationNumber; //NG
        this.growthRate = growthRate; //TC
        this.mutationRate = mutationRate; //TM
        this.generationInterval = generationInterval;  //IG

        pop = order(pop, fits);
        List<Player> initialSolution = pop.get(0);


        for (int g=0 ; g<this.gerationNumber ; g++){
            List<List<Player>> decendents = descendants(fits, pop);
            double[] fitsDecendents = new double[decendents.size()];
            fitsDecendents = fitness(decendents);
            pop = newPop(pop, order(decendents, fitsDecendents));
            fits = fitness(pop);
            pop = order(pop, fits);
        }
        List<Player> finalSolution = pop.get(0);

        return new AGResults(initialSolution, finalSolution, backpack.evaluate(initialSolution, posWeights), backpack.evaluate(finalSolution, posWeights));
    }
}




