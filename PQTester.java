/**
 * Created by austinzhang on 3/31/17.
 */
public class PQTester {

    public static void main(String[] args) {
        AustinZPriorityQueue<Integer> pq = new AustinZSimplePriorityQueue<>();
        System.out.println(pq.isEmpty());
        pq.add(141);
        pq.add(145);
        pq.add(144);
        pq.add(1234);
        pq.add(142);
        System.out.println(pq.removeMin());
        System.out.println(pq.peekMin());
        while (!pq.isEmpty()) {
            pq.removeMin();
        }
        System.out.println("done");
    }
}
