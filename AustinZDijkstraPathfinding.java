import java.util.*;

/**
 * A star Pathfinder
 * TESTED AND WORKING
 * COULD USE CLEANING
 */
public class AustinZDijkstraPathfinding {

    //INSTEAD OF VISITING EACH NODE SEPERATELY GO ONCE AND VISIT EVERY NODE

    private static final int DIAG_COST = 14;
    private static final int STRAIGHT_COST = 1;


    public LinkedList<Location> findPath(Game game, int seat, Location startLoc, Location endLoc) {

        AustinZSimplePriorityQueue open;
        LinkedList<AustinZLocationNode> closed;

        AustinZLocationNode start = new AustinZLocationNode(startLoc);
        AustinZLocationNode end = new AustinZLocationNode(endLoc);

        open = new AustinZSimplePriorityQueue();

        closed = new LinkedList<AustinZLocationNode>();

        // Add start node to open
        open.add(start);


        //loop
        while (!open.isEmpty()) {
            //Remove current cell from open and add to closed
            AustinZLocationNode current = (AustinZLocationNode) open.peekMin();
            closed.add((AustinZLocationNode) open.removeMin());

            //If path is found
            if (current.equals(end)) {
                return tracePath(current);
            }

            Set<Location> neighborLocs = game.getAvailableAdjacentLocations(current.getLocation());

            Set<AustinZLocationNode> neighbors = new HashSet<AustinZLocationNode>();
            for (Location neighborLocation : neighborLocs) {
                neighbors.add(new AustinZLocationNode(neighborLocation));
            }

            if (current.getLocation().isRoom() && game.getSecretPassage(current.getLocation().getRoom()) != -1) {
                neighbors.add(new AustinZLocationNode(new Location(game.getSecretPassage(current.getLocation().getRoom()))));
            }


            // For each neighbor of the current node
            for (AustinZLocationNode neighbor : neighbors) {

                //Make sure that the cell examined is not an obstacle and has not already been examined. And isn't the current cell
                //Nothing happens if these criteria are not met
                if (!neighbor.equals(current) && !cellInList(neighbor, closed)) {

                    //Calculate inherited score/dist from start
                    int score;

                    score = current.getCost() + STRAIGHT_COST;

                    //If the cell is in the open queue, or if the new path to the cell is less than the old one.
                    if (!cellInQueue(neighbor, open) || neighbor.getCost() > score) {

                        //sets the parent to the current node
                        neighbor.setParent(current);

                        //Assign new score
                        neighbor.setCost(score);

                        if (!cellInQueue(neighbor, open)) {
                            open.add(neighbor);
                        }
                    }

                }
            }
        }

        return null;
    }

    public int[] shortestDistanceToRooms(Game game, int seat, Location startLoc) {
        int[] distancesToRooms = new int[] {Integer.MAX_VALUE-1,Integer.MAX_VALUE-1,Integer.MAX_VALUE-1,Integer.MAX_VALUE-1,Integer.MAX_VALUE-1,Integer.MAX_VALUE-1,Integer.MAX_VALUE-1,Integer.MAX_VALUE-1,Integer.MAX_VALUE-1};

        AustinZSimplePriorityQueue open;
        LinkedList<AustinZLocationNode> closed;

        AustinZLocationNode start = new AustinZLocationNode(startLoc);

        open = new AustinZSimplePriorityQueue();

        closed = new LinkedList<AustinZLocationNode>();

        // Add start node to open
        open.add(start);


        //loop
        while (!open.isEmpty()) {
            //Remove current cell from open and add to closed
            AustinZLocationNode current = (AustinZLocationNode) open.peekMin();
            closed.add((AustinZLocationNode) open.removeMin());

            if (current.getLocation().isRoom()) {
                distancesToRooms[current.getLocation().getRoom()] = current.getCost();
            }

            //If path is found
            boolean allFound = true;
            for (int dist : distancesToRooms) {
                if (dist == Integer.MAX_VALUE - 1) {
                    allFound = false;
                }
            }

            if (allFound) {
                return distancesToRooms;
            }

            Set<Location> neighborLocs = game.getAvailableAdjacentLocations(current.getLocation());

            Set<AustinZLocationNode> neighbors = new HashSet<AustinZLocationNode>();
            for (Location neighborLocation : neighborLocs) {
                neighbors.add(new AustinZLocationNode(neighborLocation));
            }

            if (current.getLocation().isRoom() && game.getSecretPassage(current.getLocation().getRoom()) != -1) {
                neighbors.add(new AustinZLocationNode(new Location(game.getSecretPassage(current.getLocation().getRoom()))));
            }


            // For each neighbor of the current node
            for (AustinZLocationNode neighbor : neighbors) {

                //Make sure that the cell examined is not an obstacle and has not already been examined. And isn't the current cell
                //Nothing happens if these criteria are not met
                if (!neighbor.equals(current) && !cellInList(neighbor, closed)) {

                    //Calculate inherited score/dist from start
                    int score;

                    score = current.getCost() + STRAIGHT_COST;

                    //If the cell is in the open queue, or if the new path to the cell is less than the old one.
                    if (!cellInQueue(neighbor, open) || neighbor.getCost() > score) {

                        //sets the parent to the current node
                        neighbor.setParent(current);

                        //Assign new score
                        neighbor.setCost(score);

                        if (!cellInQueue(neighbor, open)) {
                            open.add(neighbor);
                        }
                    }

                }
            }
        }

        return distancesToRooms;
    }

    public int shortestDistance(Game game, int seat, Location startLoc, Location endLoc) {

        AustinZSimplePriorityQueue open;
        LinkedList<AustinZLocationNode> closed;

        AustinZLocationNode start = new AustinZLocationNode(startLoc);
        AustinZLocationNode end = new AustinZLocationNode(endLoc);

        open = new AustinZSimplePriorityQueue();

        closed = new LinkedList<AustinZLocationNode>();

        // Add start node to open
        open.add(start);


        //loop
        while (!open.isEmpty()) {
            //Remove current cell from open and add to closed
            AustinZLocationNode current = (AustinZLocationNode) open.peekMin();
            closed.add((AustinZLocationNode) open.removeMin());

            //If path is found
            if (current.equals(end)) {
                return current.getCost();
            }

            Set<Location> neighborLocs = game.getAvailableAdjacentLocations(current.getLocation());

            Set<AustinZLocationNode> neighbors = new HashSet<AustinZLocationNode>();
            for (Location neighborLocation : neighborLocs) {
                neighbors.add(new AustinZLocationNode(neighborLocation));
            }

            if (current.getLocation().isRoom() && game.getSecretPassage(current.getLocation().getRoom()) != -1) {
                neighbors.add(new AustinZLocationNode(new Location(game.getSecretPassage(current.getLocation().getRoom()))));
            }


            // For each neighbor of the current node
            for (AustinZLocationNode neighbor : neighbors) {

                //Make sure that the cell examined is not an obstacle and has not already been examined. And isn't the current cell
                //Nothing happens if these criteria are not met
                if (!neighbor.equals(current) && !cellInList(neighbor, closed)) {

                    //Calculate inherited score/dist from start
                    int score;

                    score = current.getCost() + STRAIGHT_COST;

                    //If the cell is in the open queue, or if the new path to the cell is less than the old one.
                    if (!cellInQueue(neighbor, open) || neighbor.getCost() > score) {

                        //sets the parent to the current node
                        neighbor.setParent(current);

                        //Assign new score
                        neighbor.setCost(score);

                        if (!cellInQueue(neighbor, open)) {
                            open.add(neighbor);
                        }
                    }

                }
            }
        }

        return Integer.MAX_VALUE - 1;
    }


    private LinkedList<Location> tracePath(AustinZLocationNode finalCell) {
        LinkedList<Location> path = new LinkedList<Location>();
        AustinZLocationNode currentCell = finalCell;
        while (currentCell != null) {
            path.add(0, currentCell.getLocation());
            currentCell = currentCell.getParent();
        }

        return path;
    }



    private boolean cellInList(AustinZLocationNode cell, LinkedList<AustinZLocationNode> list) {
        return list.contains(cell);
    }

    private boolean cellInQueue(AustinZLocationNode cell, AustinZSimplePriorityQueue queue) {
        return queue.contains(cell);
    }


    private int findHeuristicCost(Location start, Location end) {
        int distMax = Math.max(Math.abs(start.getX() - end.getX()), Math.abs(start.getY() - end.getY()));
        int distMin = Math.min(Math.abs(start.getX() - end.getX()), Math.abs(start.getY() - end.getY()));
        return DIAG_COST * distMin + STRAIGHT_COST * (distMax - distMin);
    }

    public Location bestMove(Game game, int seat, Set<Location> possibleMoves, int targetRoom) {
        HashSet<AustinZLocationDistancePair> locationDistancePairs = new HashSet<AustinZLocationDistancePair>();
        AustinZSimplePriorityQueue open;
        LinkedList<AustinZLocationNode> closed;

        AustinZLocationNode start = new AustinZLocationNode(new Location(targetRoom));

        open = new AustinZSimplePriorityQueue();

        closed = new LinkedList<AustinZLocationNode>();

        // Add start node to open
        open.add(start);


        //loop
        while (!open.isEmpty()) {
            //Remove current cell from open and add to closed
            AustinZLocationNode current = (AustinZLocationNode) open.peekMin();
            closed.add((AustinZLocationNode) open.removeMin());

            if (possibleMoves.contains(current.getLocation())) {
                locationDistancePairs.add(new AustinZLocationDistancePair(current.getLocation(), current.getCost()));
                possibleMoves.remove(current.getLocation());
            }

            if (possibleMoves.size() == 0) {
                int lowestCost = Integer.MAX_VALUE;
                Location nearestLocation = null;

                for (AustinZLocationDistancePair locationDistancePair : locationDistancePairs) {
                    if (locationDistancePair.getDistance() < lowestCost) {
                        lowestCost = locationDistancePair.getDistance();
                        nearestLocation = locationDistancePair.getLocation();
                    }
                }

                return nearestLocation;
            }


            Set<Location> neighborLocs = game.getAvailableAdjacentLocations(current.getLocation());

            Set<AustinZLocationNode> neighbors = new HashSet<AustinZLocationNode>();
            for (Location neighborLocation : neighborLocs) {
                neighbors.add(new AustinZLocationNode(neighborLocation));
            }

            if (current.getLocation().isRoom() && game.getSecretPassage(current.getLocation().getRoom()) != -1) {
                neighbors.add(new AustinZLocationNode(new Location(game.getSecretPassage(current.getLocation().getRoom()))));
            }


            // For each neighbor of the current node
            for (AustinZLocationNode neighbor : neighbors) {

                //Make sure that the cell examined is not an obstacle and has not already been examined. And isn't the current cell
                //Nothing happens if these criteria are not met
                if (!neighbor.equals(current) && !cellInList(neighbor, closed)) {

                    //Calculate inherited score/dist from start
                    int score;

                    score = current.getCost() + STRAIGHT_COST;

                    //If the cell is in the open queue, or if the new path to the cell is less than the old one.
                    if (!cellInQueue(neighbor, open) || neighbor.getCost() > score) {

                        //sets the parent to the current node
                        neighbor.setParent(current);

                        //Assign new score
                        neighbor.setCost(score);

                        if (!cellInQueue(neighbor, open)) {
                            open.add(neighbor);
                        }
                    }

                }
            }
        }

        //System.out.println("SOME UNREACHABLE LOCATIONS");

        int lowestCost = Integer.MAX_VALUE;
        Location nearestLocation = null;

        for (AustinZLocationDistancePair locationDistancePair : locationDistancePairs) {
            if (locationDistancePair.getDistance() < lowestCost) {
                lowestCost = locationDistancePair.getDistance();
                nearestLocation = locationDistancePair.getLocation();
            }
        }

        return nearestLocation;
    }

}
