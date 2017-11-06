/**
 * Created by austinzhang on 4/3/17.
 */
public class AustinZLocationNode implements Comparable {
    private AustinZLocationNode parentNode;
    private Location location;
    private int cost;

    public AustinZLocationNode(Location location) {
        this.location = location;
        parentNode = null;
        cost = 0;
    }

    public AustinZLocationNode getParent() {
        return parentNode;
    }

    public Location getLocation() {
        return location;
    }

    public int getCost() {
        return cost;
    }

    public void setParent(AustinZLocationNode parentNode) {
        this.parentNode = parentNode;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    @Override
    public int compareTo(Object o) {
        if (cost > ((AustinZLocationNode) o).getCost()) {
            return 1;
        }

        if (cost == ((AustinZLocationNode)o).getCost()) {
            return 0;
        }

        return -1;
    }

    @Override
    public boolean equals(Object o) {
        return getLocation().equals(((AustinZLocationNode)o).getLocation());
    }

}
