public interface AustinZPriorityQueue<E extends Comparable<E>>
{
    boolean isEmpty();
    void add(E obj);
    E peekMin();  //returns minimum value without removing it
    E removeMin();  //removes and returns minimum value
}
