public class Suggestion
{
  private int person;
  private int room;
  private int weapon;
  
  public Suggestion(int person, int room, int weapon)
  {
    //reject if invalid
    if (person < 0 || person >= 6 ||
        room < 0 || room >= 9 ||
        weapon < 0 || weapon >= 6)
      throw new RuntimeException("invalid suggestion:  " + person + ", " + room + ", " + weapon);
    
    this.person = person;
    this.room = room;
    this.weapon = weapon;
  }
  
  public int getPerson()
  {
    return person;
  }

  public int getRoom()
  {
    return room;
  }
  
  public int getWeapon()
  {
    return weapon;
  }
  
  public boolean contains(Card card)
  {
    if (card.getType() == Card.PERSON)
      return card.getValue() == person;
    else if (card.getType() == Card.ROOM)
      return card.getValue() == room;
    else if (card.getType() == Card.WEAPON)
      return card.getValue() == weapon;
    else
      throw new RuntimeException("invalid card type:  " + card);
  }

  public boolean equals(Object obj)
  {
    Suggestion other = (Suggestion)obj;
    return person == other.getPerson() && room == other.getRoom() && weapon == other.getWeapon();
  }
  
  public String toString()
  {
    return Card.getPersonName(person) +
      " in the " + Card.getRoomName(room) +
      " with the " + Card.getWeaponName(weapon);
  }
  
  public int hashCode()
  {
    return 100 * person + 10 * room + weapon;
  } 
}
