import java.io.*;
import java.util.*;

/**
 * Created by austinzhang on 4/24/17.
 */
public class GeneticOptimizer {
    public static final double MUTATION_RATE = .15;
    public static final int POPULATION_SIZE = 200;
    public static final double ELITISM_RATE = .1;
    public static final int NUM_CARRYOVER = (int)(POPULATION_SIZE * ELITISM_RATE);
    public static final int NUM_GENERATIONS = 100;
    public static final int NUM_TRIALS_PER_FITNESS_EVAL = 200;
    public static final double SIX_PLAYER_PROBABILITY = .75;
    public static final int NUM_CROSSBRED = (POPULATION_SIZE - ((int)(ELITISM_RATE * POPULATION_SIZE))) / 2;

    private static Strategy[] trainingStrategies = new Strategy[]{new AustinZBasicStrategy(), new AustinZBasicStrategy()};

    public static void main(String[] args) throws IOException, InterruptedException {
        //Initialize

        //Generate the first population
        HashSet<Organism> population = getStartingPopulation();

        createGeneration(population, 0);
    }

    public static void createGeneration(HashSet<Organism> population, int numGenerations) throws IOException, InterruptedException {
        if (numGenerations >= NUM_GENERATIONS) {
            return;
        }

        FitnessCalculationThread calc1 = new FitnessCalculationThread();
        FitnessCalculationThread calc2 = new FitnessCalculationThread();
        FitnessCalculationThread calc3 = new FitnessCalculationThread();
        FitnessCalculationThread calc4 = new FitnessCalculationThread();

        calc1.start();
        calc2.start();
        calc3.start();
        calc4.start();

        //Get the fitness of each organism in this population
        int loopnum = 0;
        for (Organism organism : population) {
            loopnum++;
            boolean orgLoaded = false;


            while (!orgLoaded) {
                if (!calc1.isCalculatingFitness()) {
                    calc1.setOrganism(organism);
                    orgLoaded = true;
                } else if (!calc2.isCalculatingFitness()) {
                    calc2.setOrganism(organism);
                    orgLoaded = true;
                } else if (!calc3.isCalculatingFitness()) {
                    calc3.setOrganism(organism);
                    orgLoaded = true;
                } else if (!calc4.isCalculatingFitness()) {
                    calc4.setOrganism(organism);
                    orgLoaded = true;
                }

                Thread.sleep(20);
            }
        }

        while (calc1.isCalculatingFitness() || calc2.isCalculatingFitness() || calc3.isCalculatingFitness() || calc4.isCalculatingFitness()) {
            Thread.sleep(1);
        }

        calc1.stopRunning();
        calc2.stopRunning();
        calc3.stopRunning();
        calc4.stopRunning();


        //Create a list that we will use later to choose which organisms to crossbreed and carry over to the next generation
        Organism[] populationArray = population.toArray(new Organism[population.size()]);
        Quicksort.sort(populationArray);

        HashSet<Organism> newPopulation = new HashSet<Organism>();


        //Carry over the best of the last generation
        for (int i = populationArray.length - 1; i >= populationArray.length * (1 - ELITISM_RATE); i--) {
            newPopulation.add(populationArray[i]);
        }


        /** VERY BAD WAY OF CHOOSING ORGANISMS FOR CROSSBREEDING -- FIND BETTER WAY **/
        /** SUPER INEFFICIENT **/
        LinkedList<Organism> crossbreedPool = new LinkedList<Organism>();

        for (int i = 0; i < populationArray.length; i++) {
            for (int p = 0; p <= i; p++) {
                crossbreedPool.add(populationArray[i]);
            }
        }

        //Add crossbreed individuals to new population
        while (newPopulation.size() < NUM_CROSSBRED + NUM_CARRYOVER) {
            Organism parent1 = crossbreedPool.get((int)(Math.random() * crossbreedPool.size()));
            Organism parent2 = crossbreedPool.get((int)(Math.random() * crossbreedPool.size()));
            newPopulation.add(parent1.crossover(parent2));
        }

        //Add mutated individuals to new population

        while (newPopulation.size() < POPULATION_SIZE) {
            newPopulation.add(populationArray[(int)(Math.random() * populationArray.length)].mutate(MUTATION_RATE));
        }

        Organism mostFitOrganism = populationArray[populationArray.length - 1];

        //Stores fittest organism of last population and current population.
        storeResults(mostFitOrganism, newPopulation);

        System.out.println("New Generation." + "Current Generation: " + numGenerations);

        createGeneration(newPopulation, numGenerations + 1);
    }

    private static HashSet<Organism> getStartingPopulation() throws IOException {
        String currentDirectoryName = new File(".").getAbsolutePath().substring(0, new File(".").getAbsolutePath().lastIndexOf("."));

        new File(currentDirectoryName + "/genetic_optimizer.txt").createNewFile();

        BufferedReader reader = new BufferedReader(new FileReader(new File(currentDirectoryName + "/genetic_optimizer.txt")));

        String line = reader.readLine();

        if (line == null) {
            System.out.println("Population Generated");
            return generatePopulation();
        } else {
            System.out.println("Population Read");
            return readPopulation(reader);
        }

    }

    private static HashSet<Organism> generatePopulation() {
        HashSet<Organism> population = new HashSet<Organism>();

        while (population.size() < POPULATION_SIZE) {
            population.add(Organism.randomlyGenerateOrganism());
        }

        return population;
    }

    public static HashSet<Organism> readPopulation(BufferedReader reader) throws IOException {
        HashSet<Organism> population = new HashSet<>();

        String line = reader.readLine();

        while (line != null) {
            ArrayList<Integer> organismTraits = new ArrayList<Integer>();
            Scanner scanner = new Scanner(line);
            while (scanner.hasNext()) {
                organismTraits.add(Integer.parseInt(scanner.next()));
            }

            int[] organismTraitsArray = new int[organismTraits.size()];

            for (int i = 0; i < organismTraits.size(); i++) {
                organismTraitsArray[i] = organismTraits.get(i);
            }

            population.add(new Organism(organismTraitsArray));

            line = reader.readLine();
        }

        return population;
    }

    //Gets the percentage of wins of a given organism
    public static double getFitness(Organism organism, int numTrials) {

        Strategy strategy = new AustinZBasicStrategy(organism.getChromosomes());

        int numWins = 0;

        for (int i = 0; i < numTrials; i++) {
            int numOpponents;
            if (Math.random() < SIX_PLAYER_PROBABILITY) {
                numOpponents = 5;
            } else {
                numOpponents = 2;
            }

            Strategy[] stratArray = new Strategy[numOpponents + 1];

            for (int t = 0; t < numOpponents; t++) {
                if (Math.random() < .5) {
                    stratArray[t] = new AustinZOldStrat();
                }
                else {
                    stratArray[t] = new AustinZBasicStrategy();
                }
            }

            stratArray[stratArray.length - 1] = strategy;

            //System.out.println(Arrays.toString(stratArray));

            Game game = new Game(stratArray, null);
            Strategy winningStrat = game.play();
            if (winningStrat != null && winningStrat.equals(strategy)) {
                //System.out.println("WINNER");
                numWins++;
            } else {
                //System.out.println("LOSER");
            }
        }

        System.out.println(organism.toString());
        System.out.println((double) numWins / numTrials);

        return ((double) numWins) / numTrials;
    }

    private static void storeResults(Organism fittestOrganism, HashSet<Organism> organisms) throws IOException {
        String currentDirectoryName = new File(".").getAbsolutePath().substring(0, new File(".").getAbsolutePath().lastIndexOf("."));
        new File(currentDirectoryName + "/genetic_optimizer.txt").createNewFile();
        PrintWriter writer = new PrintWriter(new FileWriter(new File(currentDirectoryName + "/genetic_optimizer.txt")));
        writer.println("Current Fittest- " + "Fitness: " + fittestOrganism.getFitness() + "Chromosomes " + fittestOrganism.toString());
        for (Organism organism : organisms) {
            writer.println(organism.toString());
        }

        writer.close();
    }

}
