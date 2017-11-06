/**
 * Created by austinzhang on 4/27/17.
 */
public class FitnessCalculationThread extends Thread {

    Organism organism;
    boolean calculatingFitness;
    boolean keepRunning;
    int numTrials;

    public FitnessCalculationThread() {
        organism = null;
        calculatingFitness = false;
        keepRunning = true;
        numTrials = GeneticOptimizer.NUM_TRIALS_PER_FITNESS_EVAL;
    }

    public FitnessCalculationThread(int numTrials) {
        organism = null;
        calculatingFitness = false;
        keepRunning = true;
        this.numTrials = numTrials;
    }

    @Override
    public void run() {
        while (keepRunning) {

            if (organism == null) {
                calculatingFitness = false;
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else {
                calculatingFitness = true;
                organism.setFitness(GeneticOptimizer.getFitness(organism, numTrials));
                organism = null;
                calculatingFitness = false;
            }

        }
    }

    public boolean isCalculatingFitness() {
        return calculatingFitness;
    }

    public void stopRunning() {
        keepRunning = false;
    }

    public void setOrganism(Organism organism) {
        this.organism = organism;
    }


}
