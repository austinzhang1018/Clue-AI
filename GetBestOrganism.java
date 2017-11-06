import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

/**
 * Created by austinzhang on 4/28/17.
 */
public class GetBestOrganism {
    private static final int NUM_TRIALS = 5000;

    public static void main(String[] args) throws IOException, InterruptedException {
        String currentDirectoryName = new File(".").getAbsolutePath().substring(0, new File(".").getAbsolutePath().lastIndexOf("."));

        new File(currentDirectoryName + "/genetic_optimizer.txt").createNewFile();

        BufferedReader reader = new BufferedReader(new FileReader(new File(currentDirectoryName + "/genetic_optimizer.txt")));

        String line = reader.readLine();

        if (line == null) {
            throw new RuntimeException("NO FILE");
        }

        HashSet<Organism> population = GeneticOptimizer.readPopulation(reader);

        FitnessCalculationThread calc1 = new FitnessCalculationThread(NUM_TRIALS);
        FitnessCalculationThread calc2 = new FitnessCalculationThread(NUM_TRIALS);
        FitnessCalculationThread calc3 = new FitnessCalculationThread(NUM_TRIALS);
        FitnessCalculationThread calc4 = new FitnessCalculationThread(NUM_TRIALS);

        calc1.start();
        calc2.start();
        calc3.start();
        calc4.start();

        //Get the fitness of each organism in this population
        for (Organism organism : population) {
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

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();

        for (Organism organism: populationArray) {
            System.out.println("Org: " + organism.toString());
            System.out.println("Fit: " + organism.getFitness());
        }

    }
}
