import java.util.HashSet;

/**
 * Created by austinzhang on 4/23/17.
 */
public class BitTester {

    public static void main(String[] args) {

        int[] chro1 = {4123, 1320, 50, 8, 7};

        Organism org1 = new Organism(chro1);

        System.out.println(org1.mutate(.17).toString());

        /*
        HashSet<Organism> organisms = new HashSet<Organism>();


        for (int i = 0; i < 500000; i++) {
            int[] integer = new int[5];

            for (int p = 0; p < 5; p++) {
                integer[p] = (int) (Math.random() * 50);
            }

            organisms.add(new Organism(integer));
        }

        Organism lastOrg = null;
        for (Organism organism : organisms) {
            if (lastOrg != null) {
                System.out.println("|");
                System.out.println(lastOrg.toString());
                System.out.println(organism.toString());
                System.out.println(organism.crossover(lastOrg).toString());
            }

            lastOrg = organism;
        }
        */

    }
}
