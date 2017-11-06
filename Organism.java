import java.util.Arrays;
import java.util.zip.CRC32;

/**
 * Created by austinzhang on 4/24/17.
 */
public class Organism implements Comparable {

    public static final int NUM_CHROMOSOMES = 5;

    private int[] chromosomes;
    private Double fitness;

    private static int[] LOWER_RANGE = new int[] {0, 0, 0, 0, 0};
    private static int[] UPPER_RANGE = new int[] {5000, 5000, 5000, 100, 200};
    private static int[] BITS_IN_STRING;


    public Organism(int roomDistanceChromosome, int cardDistanceChromosome, int peopleCloseToRoomsChromosome, int suggestedCardChromosome, int startObservationChromosome, int guessThreshold) {
        chromosomes = new int[]{roomDistanceChromosome, cardDistanceChromosome, peopleCloseToRoomsChromosome, suggestedCardChromosome, startObservationChromosome, guessThreshold};
        fitness = -1.;

        BITS_IN_STRING = new int[UPPER_RANGE.length];

        for (int i = 0; i < BITS_IN_STRING.length; i++) {
            int startingVal = 0;
            int counter = 0;

            while (startingVal < UPPER_RANGE[i] + 1) {
                startingVal = (int) Math.pow(2, counter + 1);
                counter++;
            }

            BITS_IN_STRING[i] = counter;
        }

    }

    public Organism(int[] chromosomes) {
        this.chromosomes = chromosomes;
        fitness = -1.;

        BITS_IN_STRING = new int[UPPER_RANGE.length];

        for (int i = 0; i < BITS_IN_STRING.length; i++) {
            int startingVal = 0;
            int counter = 0;

            while (startingVal < UPPER_RANGE[i] + 1) {
                startingVal = (int) Math.pow(2, counter + 1);
                counter++;
            }

            //System.out.println("Num: " + UPPER_RANGE[i]);
            //System.out.println(counter);
            BITS_IN_STRING[i] = counter;
        }
    }

    public int getRoomDistanceChromosome() {
        return chromosomes[0];
    }

    public int getCardDistanceChromosome() {
        return chromosomes[1];
    }

    public int getSuggestedRoomChromosome() {
        return chromosomes[2];
    }

    public int getStartObservationChromosome() {
        return chromosomes[3];
    }

    public int getGuess() {
        return chromosomes[4];
    }

    public Organism crossover(Organism organism) {
        int[] childChromosomes = new int[getChromosomes().length];

        for (int i = 0; i < getChromosomes().length; i++) {
            if (Math.random() > .5) {
                childChromosomes[i] = this.getChromosomes()[i];
            } else {
                childChromosomes[i] = organism.getChromosomes()[i];
            }
        }

        return new Organism(childChromosomes);
    }

    //Change the value in the chromosomes depending on the mutation rate.
    public Organism mutate(double mutationRate) {
        int[] childChromosomes = new int[getChromosomes().length];



        for (int i = 0; i < getChromosomes().length; i++) {
            String byteChromosome = Integer.toBinaryString(getChromosomes()[i]);

            while (byteChromosome.length() < BITS_IN_STRING[i]) {
                byteChromosome = "0" + byteChromosome;
            }

            //System.out.println(byteChromosome);

            StringBuilder childByteChromosome = new StringBuilder();
            for (int c = 0; c < byteChromosome.length(); c++) {
                if (Math.random() < mutationRate) {
                    if (byteChromosome.substring(c, c + 1).equals("0"))
                        childByteChromosome.append("1");
                    else
                        childByteChromosome.append("0");
                }
                else {
                    childByteChromosome.append(byteChromosome.substring(c, c + 1));
                }
            }

            childChromosomes[i] = Integer.parseInt(childByteChromosome.toString(), 2);
        }

        boolean allValuesWithinRange = true;

        for (int i = 0; i < childChromosomes.length; i++) {
            if (childChromosomes[i] > UPPER_RANGE[i] || childChromosomes[i] < LOWER_RANGE[i]) {
                allValuesWithinRange = false;
            }
        }

        if (!allValuesWithinRange) {
            return mutate(mutationRate);
        }

        return new Organism(childChromosomes);
    }

    public String toString() {
        StringBuilder string = new StringBuilder();

        for (int chromosome : getChromosomes()) {
            string.append(chromosome).append(" ");
        }

        return string.toString();

    }

    public int[] getChromosomes() {
        return chromosomes;
    }

    public static Organism randomlyGenerateOrganism() {
        int[] traits = new int[NUM_CHROMOSOMES];

        for (int i = 0; i < NUM_CHROMOSOMES; i++) {
            traits[i] = ((int) (Math.random() * (UPPER_RANGE[i] - LOWER_RANGE[i] + 1))) + LOWER_RANGE[i];
        }

        return new Organism(traits);
    }

    @Override
    public boolean equals(Object other) {
        for (int i = 0; i < this.getChromosomes().length; i++) {
            if (this.getChromosomes()[i] != ((Organism)other).getChromosomes()[i]) {
                return false;
            }
        }
        return true;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public Double getFitness() {
        return fitness;
    }

    @Override
    public int hashCode() {
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < getChromosomes().length; i++) {
            string.append(getChromosomes()[i]);
        }

        CRC32 hashCode = new CRC32();
        hashCode.update(string.toString().getBytes());

        long hashValue = hashCode.getValue();

        while (hashValue > Integer.MAX_VALUE) {
            hashValue -= Integer.MAX_VALUE;
        }

        while (hashValue < Integer.MAX_VALUE) {
            hashValue += Integer.MAX_VALUE;
        }

        return (int) hashValue;
    }

    public void setChromosome(int index, int value) {
        chromosomes[index] = value;
    }


    @Override
    public int compareTo(Object o) {
        return this.getFitness().compareTo(((Organism) o).getFitness());
    }
}
