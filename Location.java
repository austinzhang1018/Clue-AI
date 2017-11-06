public class Location implements Comparable<Location>
{
  //locations corresponding to rooms have x value of ROOM_X and y value is Card.BALLROOM, etc.
  private static int ROOM_X = 50;
  
  private int xCoordinate;
  private int yCoordinate;
  
  //the game board is labeled under the Cartesian coordinate system where (0,0) in the study is the top left corner and (23,24) is the bottom right corner
  
  public Location(int x, int y)
  {
    xCoordinate = x;
    yCoordinate = y;
  }
  
  public Location(int room)
  {
    xCoordinate = ROOM_X;
    yCoordinate = room;
  }
  
  public int getX()
  {
    if (isRoom())
      throw new RuntimeException("attempt to get x from room");
    else
      return xCoordinate;
  }
  
  public int getY()
  {
    if (isRoom())
      throw new RuntimeException("attempt to get y from room");
    else
      return yCoordinate;
  }
  
  public String toString()
  {
    if (isRoom())
      return Card.getRoomName(yCoordinate);
    else
      return "(" + xCoordinate + ", " + yCoordinate + ")";
  }
  
  public int hashCode()
  {
    return (xCoordinate * 100) + yCoordinate;
  }
  
  public boolean equals(Object o)
  {
    Location loc = (Location)o;
    if (isRoom() && loc.isRoom())
      return getRoom() == loc.getRoom();
    else if (!isRoom() && !loc.isRoom())
      return getX() == loc.getX() && getY() == loc.getY();
    else
      return false;  //one is room and other isn't
  }
  
  public boolean isRoom()
  {
    return xCoordinate == ROOM_X;
  }
  
  public int getRoom()
  {
    if (isRoom())
      return yCoordinate;
    else
      throw new RuntimeException("not a room");
  }
  
  public int compareTo(Location other)
  {
    if (xCoordinate != other.xCoordinate)
      return yCoordinate - other.yCoordinate;
    else
      return xCoordinate - other.xCoordinate;
  }
}