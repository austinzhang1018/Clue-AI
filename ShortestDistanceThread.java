import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by austinzhang on 4/5/17.
 */
public class ShortestDistanceThread implements Runnable {
    private int shortestDistance = -1;

    private boolean isReady = false;

    private AustinZDijkstraPathfinding pathfinder;
    private Game game;
    private int seat;
    private ArrayList<Location> startLocs;
    private ArrayList<Location> endLocs;
    private ArrayList<Map<Location, Integer>> roomDistancePair = new ArrayList<Map<Location, Integer>>();

    @Override
    public void run() {
        for (int i = 0; i < roomDistancePair.size(); i++) {
            roomDistancePair.get(i).put(endLocs.get(i), pathfinder.shortestDistance(game, seat, startLocs.get(i), endLocs.get(i)));
        }
        isReady = true;
    }

    public void loadParameters(AustinZDijkstraPathfinding pathfinder, Game game, int seat) {
        this.pathfinder = pathfinder;
        this.game = game;
        this.seat = seat;
    }

    public void loadParameters(ArrayList<Location> startLocs, ArrayList<Location> endLocs) {
        this.startLocs = startLocs;
        this.endLocs = endLocs;

        for (int i = 0; i < startLocs.size(); i++) {
            roomDistancePair.add(new HashMap<Location, Integer>());
            roomDistancePair.get(i).put(endLocs.get(i), -1);
        }


    }

    public boolean isReady() {
        return isReady;
    }

    public ArrayList<Map<Location, Integer>> getShortestDistances() {
        return roomDistancePair;
    }
}