/**
 * Individual units in fields
 */
public class Cell implements Comparable {

    int gCost; //Cost from start
    int finalCost; //G+H
    Location location;
    Cell parent;

    Cell(Location location) {
        this.location = location;
        gCost = 0;
        finalCost = 0;
    }

    @Override
    public String toString() {
        return "[" + this.location.getX() + ", " + this.location.getY() + "]";
    }


    @Override
    public int compareTo(Object o) {
        if (finalCost > ((Cell) o).finalCost) {
            return 1;
        }

        if (finalCost == ((Cell)o).finalCost) {
            return 0;
        }

        return -1;
    }

}
