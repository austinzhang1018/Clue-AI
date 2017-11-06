public class Card implements Comparable<Card>
{
  public static final int PERSON = 1;
  public static final int ROOM = 2;
  public static final int WEAPON = 3;

  public static final int SCARLET = 0;
  public static final int MUSTARD = 1;
  public static final int WHITE = 2;
  public static final int GREEN = 3;
  public static final int PEACOCK = 4;
  public static final int PLUM = 5;
  
  public static final int CANDLESTICK = 0;
  public static final int KNIFE = 1;
  public static final int PIPE = 2;
  public static final int REVOLVER = 3;
  public static final int ROPE = 4;
  public static final int WRENCH = 5;
  
  public static final int BALLROOM = 0;
  public static final int CONSERVATORY = 1;
  public static final int BILLIARDROOM = 2;
  public static final int LIBRARY = 3;
  public static final int STUDY = 4;
  public static final int HALL = 5;
  public static final int LOUNGE = 6;
  public static final int DININGROOM = 7;
  public static final int KITCHEN = 8;
  
  private static final String[] PERSON_NAMES = {
    "Miss Scarlet", "Colonel Mustard", "Mrs. White",
    "Mr. Green", "Mrs. Peacock", "Professor Plum"};
  
  private static final String[] ROOM_NAMES = {
    "Ballroom", "Conservatory", "Billard Room", "Library",
    "Study", "Hall", "Lounge", "Dining Room", "Kitchen"};
  
  private static final String[] WEAPON_NAMES = {
    "Candlestick", "Knife", "Lead Pipe",
    "Revolver", "Rope", "Wrench"};
  
  private int type;
  private int value;

  public Card(int type, int value)
  {
    this.type = type;
    this.value = value;
  }
  
  public int getType()
  {
    return type;
  }
  public int getValue()
  {
    return value;
  }
  
  public boolean isWeapon()
  {
    if (type == 3)
      return true;
    else
      return false; 
  }
  
  public boolean isPerson()
  {
    if (type == 1)
      return true;
    else
      return false;
  }
  
  
  public boolean isRoom()
  {
    if (type == 2)
      return true;
    else
      return false;
  }
  
  
  public static String getPersonName(int person)
  {
    return PERSON_NAMES[person];
  }
  
  public static String getRoomName(int room)
  {
    return ROOM_NAMES[room];
  }
  
  public static String getWeaponName(int weapon)
  {
    return WEAPON_NAMES[weapon];
  }
  
  public String toString()
  {
    if (isPerson())
      return getPersonName(value);
    else if (isRoom())
      return getRoomName(value);
    else if (isWeapon())
      return getWeaponName(value);
    else
      throw new RuntimeException("invalid card type:  " + type);
  }
  
  public boolean equals(Object obj)
  {
    Card other = (Card)obj;
    return type == other.getType() && value == other.getValue();
  }
  
  public int hashCode()
  {
    return type * 10 + value;
  }
  
  public int compareTo(Card other)
  {
    if (getType() != other.getType())
      return getType() - other.getType();
    else
      return getValue() - other.getValue();
  }
}

