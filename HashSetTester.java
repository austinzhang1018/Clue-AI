import java.util.HashSet;

/**
 * Created by austinzhang on 3/31/17.
 */
public class HashSetTester {

    public static void main (String args[]) {
        HashSet<Location> test = new HashSet<>();

        test.add(new Location(1, 4));
        test.add(new Location(1, 4));
        test.add(new Location(1, 4));
        test.add(new Location(1, 4));
        test.add(new Location(1, 4));
        test.add(new Location(1, 4));
        test.add(new Location(1, 4));
        test.add(new Location(1, 4));
        test.add(new Location(1, 4));
        test.add(new Location(1, 4));
        test.add(new Location(1));
        test.add(new Location(1));
        test.add(new Location(1));
        test.add(new Location(1));
        test.add(new Location(1));


        System.out.println(test.size());
    }
}
