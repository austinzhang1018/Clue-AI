import java.io.*;
import java.util.ArrayList;

/**
 * Created by austinzhang on 4/12/17.
 */
public class Optimizer {
    public static void main(String[] args) throws IOException {
        optimize();
    }

    private static void optimize() throws IOException {
        int numTrials = 2000;
        double LARGE_STEP = 2;
        double SMALL_STEP = 1;
        double MAX = 50;
        double MIN = 0;

        String currentDirectoryName = new File(".").getAbsolutePath().substring(0, new File(".").getAbsolutePath().lastIndexOf("."));

        new File(currentDirectoryName + "/room_balancing.txt").createNewFile();

        BufferedReader reader = new BufferedReader(new FileReader(new File(currentDirectoryName + "/room_balancing.txt")));

        ArrayList<String> strings = new ArrayList<String>();

        double currentCoeff = -1;

        String line = reader.readLine();
        while (line != null) {
            strings.add(line);
            currentCoeff = Double.parseDouble(line.substring(0, line.indexOf(" ")));
            line = reader.readLine();
        }
        reader.close();

        if (currentCoeff == -1) {
            currentCoeff = 15;
        }

        System.out.println("Starting Coeff: " + currentCoeff);

        PrintWriter writer = new PrintWriter(new FileWriter(new File(currentDirectoryName + "/room_balancing.txt")), true);

        for (String dataLine : strings) {
            writer.println(dataLine);
        }

        while (true) {

            double winPercentage[] = new double[6];
            double coeffs[] = new double[winPercentage.length];

            coeffs[0] = currentCoeff;
            coeffs[1] = currentCoeff + LARGE_STEP;
            coeffs[2] = currentCoeff + SMALL_STEP;
            coeffs[3] = currentCoeff - LARGE_STEP;
            coeffs[4] = currentCoeff - SMALL_STEP;
            coeffs[5] = (Math.random() * (MAX - MIN)) + MIN;

            for (int i = 0; i < coeffs.length; i++) {
            }

            double maxPercentage = Double.MIN_VALUE;
            double maxCoeff = -1;
            for (int i = 0; i < winPercentage.length; i++) {
                if (winPercentage[i] > maxPercentage) {
                    maxPercentage = winPercentage[i];
                    maxCoeff = coeffs[i];
                }
            }

            if (maxCoeff == coeffs[5]) {
                for (int i = 0; i < coeffs.length; i++) {
                }

                maxPercentage = Double.MIN_VALUE;
                maxCoeff = -1;
                for (int i = 0; i < winPercentage.length; i++) {
                    if (winPercentage[i] > maxPercentage) {
                        maxPercentage = winPercentage[i];
                        maxCoeff = coeffs[i];
                    }
                }


            }

            currentCoeff = maxCoeff;
            writer.println(maxCoeff + " " + maxPercentage);
            System.out.println(maxCoeff + " " + maxPercentage);
        }


    }

}
