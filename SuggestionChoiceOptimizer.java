import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by austinzhang on 3/25/17.
 * Hill climbing algorithm to find best suggestion choice
 *
 */
public class SuggestionChoiceOptimizer {


    private static double LOWER_BOUND = 10;
    private static double UPPER_BOUND = 110;
    private static double STEP = 1;
    private static int NUM_TRIALS = 750;
    private double coeffVal;
    private int[] wins = new int[6];
    private ArrayList<CoeffWinPair> winPairs = new ArrayList<>();

    private int counter;

    public static void main(String args[]) throws IOException {
        SuggestionChoiceOptimizer optimizer = new SuggestionChoiceOptimizer();
        optimizer.runOptimization();
    }



    public void runOptimization() throws IOException {
        String currentDirectoryName = new File(".").getAbsolutePath().substring(0, new File(".").getAbsolutePath().lastIndexOf("."));
        PrintWriter writer = new PrintWriter(new FileWriter(new File(currentDirectoryName + "/room_balancing.txt")), true);

        long startTime = System.currentTimeMillis();

        for (double i = LOWER_BOUND; i <= UPPER_BOUND; i = i + STEP) {
            setCoeffValue(14.5);

            for (int trialNum = 0; trialNum < NUM_TRIALS; trialNum++) {
                //new Game(new Strategy[]{new RandomStrategy(), new RandomStrategy(), new RandomStrategy(), new RandomStrategy(), new RandomStrategy(), new AustinZBasicStrategy()}, this).play();
            }

            writer.println(coeffVal + " " + ((double)wins[5]) / NUM_TRIALS);
            System.out.println(coeffVal + " " + ((double)wins[5]) / NUM_TRIALS);
            winPairs.add(new CoeffWinPair(coeffVal, ((double)wins[5]) / NUM_TRIALS));

            wins = new int[6];
        }

        double coeffOfMostWins = 0;
        double valOfMostWins = -1;

        for (int i = 0; i < winPairs.size(); i++) {
            if (winPairs.get(i).winPercentage > valOfMostWins) {
                valOfMostWins = winPairs.get(i).winPercentage;
                coeffOfMostWins = winPairs.get(i).getCoeff();
            }
        }

        writer.println("Optimal Coeff: " + coeffOfMostWins + " WinRate: " + valOfMostWins);

        System.out.println("Optimal Coeff: " + coeffOfMostWins + " WinRate: " + valOfMostWins);
        System.out.println("Total Time: " + (System.currentTimeMillis() - startTime) /1000);

    }

    public void standardDeviationFinder() {

    }

    public void reportWinner (int winner) {
        wins[winner]++;
    }

    public void reportTurns(int turns) {
        counter = counter + turns;
    }

    private void setCoeffValue(double coeffValue) {
        coeffVal = coeffValue;
    }

    public double getCoeff() {
        return coeffVal;
    }
}
