public class Player
{
  private Strategy strategy;
  private int person;  //(character)
  private Location loc;  //may be hallway square or room
  private Card[] cards;
  private boolean recentlyTransported;
  private boolean isAlive;
  private int seat;
  private Game game;
  
  public Player(Game game, int seat, Strategy strategy, int person, Location loc, Card[] cards)
  {
    this.game = game;
    this.strategy = strategy;
    this.person = person;
    this.loc = loc;
    this.seat = seat;
    
    recentlyTransported = false;
    isAlive = true;
    
    //for security reasons, we don't want player or strategy to share a mutable array
    this.cards = new Card[cards.length];
    for (int i = 0; i < cards.length; i++)
      this.cards[i] = cards[i];
  }
  
  //game calls only once per player, after everything has been set up
  public void init()
  {
    Card[] strategyCards = new Card[cards.length];
    for (int i = 0; i < cards.length; i++)
      strategyCards[i] = cards[i];
    strategy.init(game, seat, strategyCards);
  }
  
  public Strategy getStrategy()
  {
    return strategy;
  }
  
  public int getPerson()
  {
    return person;
  }
  
  public Location getLocation()
  {
    return loc;
  }
  
  //pre:  loc is valid hallway square or room
  public void setLocation(Location loc)
  {
    this.loc = loc;
  }
  
  public boolean hasCard(Card card)
  {
    for (Card c : cards)
    {
      if (c.equals(card))
        return true;
    }
    return false;
  }
  
  public Card[] getCards()
  {
    return cards;
  }
  
  public boolean recentlyTransported()
  {
    return recentlyTransported;
  }
  
  public void setTransported(boolean transported)
  {
    recentlyTransported = transported;
  }
  
  public boolean isAlive()
  {
    return isAlive;
  }
  
  public void die()
  {
    isAlive = false;
  }
  
  public int getNumCards()
  {
    return cards.length;
  }
  
  public String toString()
  {
    return strategy.toString() + "(" + seat + ")";//, " + Card.getPersonName(person) + ")";
  }
}