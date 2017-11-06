import java.util.*;

public class RandomStrategy implements Strategy
{ 
  private Game game;
  private int seat;
  private Card[] cards;
  private ArrayList<Card> knownCards;
  private ArrayList<Suggestion> knownSuggestions;
  private Suggestion winningSuggestion;
  private Random random;
  private Location oldLocation; //for testing
  
  //called at start of game
  public void init(Game game, int seat, Card[] cards)
  {
    //System.out.println("Seat " + seat + ": " + this.getClass().getName());
    this.game = game;
    this.seat = seat;
    this.cards = cards;
    this.knownCards = new ArrayList<Card>();
    this.knownSuggestions = new ArrayList<Suggestion>();
    this.winningSuggestion = null;
    this.random = new Random();
    for (Card card : cards)
    {
      this.knownCards.add(card);
    }
  }
  
  //questions

  //returns ACCUSE, STAY, ROLL, or SECRET_PASSAGE
  public int getActionType(boolean canStay, boolean canUseSecretPassage)
  {
    if (winningSuggestion != null)
    {
      return Strategy.ACCUSE;
    }
    else if (canStay && game.getLocation(seat).isRoom() && !hasCard(Card.ROOM, game.getLocation(seat).getRoom()))
    {
      return Strategy.STAY;
    }
    else
      return Strategy.ROLL;
  }

  //returns coordinates you moved to (could be door)
  //if door, then player enters a room
  //not called if cannot move?
  public Location move(int roll)
  {    
    Set<Location> validLocs = game.getDestinations(game.getLocation(seat), roll);
    if (validLocs.size() != 0)
    {
      int value = random.nextInt(validLocs.size());
      int i = 0;
      for (Location l : validLocs)
      {
        if (value == i)
          return l;
        i++;
      }
      throw new IllegalStateException("Failed to get random location in non-empty set");
    }
    else if (!game.getLocation(seat).isRoom())
    {
      System.err.println(seat + " can't move");
      return game.getLocation(seat);
    }
    else
    {
      System.err.println(seat + " can't move in room");
      return game.getLocation(seat);
    }
  }

  //cannot be null
  public Suggestion getSuggestion()
  {
    if (winningSuggestion != null)
      return winningSuggestion;
    return getNextSuggestion();
  }

  public Card revealCard(int suggester, Suggestion suggestion)
  {
    for (Card card : cards)
    {
      if (suggestion.contains(card))
        return card;
    }
    throw new IllegalStateException("Asked to reveal card to counter " + suggestion + " but given no valid card");
  }
  
  //can return null if just suggested and no card revealed and now choose not to accuse
  public Suggestion getAccusation()
  {
    if (winningSuggestion != null)
      return winningSuggestion;
    else
      return null;
  }
  
  //alerts
  
  //tells player result of another player's suggestion
  //revealer is -1 if no card revealed
  public void suggestionMade(int suggester, Suggestion suggestion, int revealer)
  {
    if (!knownSuggestions.contains(suggestion))
      knownSuggestions.add(suggestion);
    if (revealer == -1)
      winningSuggestion = suggestion;
  }
  
  //tells player result of their suggestion
  //revealer is -1 and card is null if no card revealed
  public void cardRevealed(Suggestion suggestion, int revealer, Card card)
  {
    if (card != null)
      knownCards.add(card);
  }
  
  //someone made a bad accusation
  public void accusationMade(int accuser, Suggestion accusation)
  {
    if (!knownSuggestions.contains(accusation))
      knownSuggestions.add(accusation);
  }
  
  /*
   * Helper Methods
   */
  
  private Suggestion getNextSuggestion()
  {
    int person = 0;
    int room = 0;
    int weapon = 0;
    int[] values = getSuggestionFrom(person, room, weapon);
    person = values[0];
    room = values[1];
    weapon = values[2];
    Suggestion suggestion = new Suggestion(person, room, weapon);
    while (knownSuggestions.contains(suggestion))
    {
      if (person < 5)
      {
        person++;
        values = getSuggestionFrom(person, room, weapon);
        person = values[0];
        room = values[1];
        weapon = values[2];
        suggestion = new Suggestion(person, room, weapon);
      }
      else if (weapon < 5)
      {
        weapon++;
        values = getSuggestionFrom(person, room, weapon);
        person = values[0];
        room = values[1];
        weapon = values[2];
        suggestion = new Suggestion(person, room, weapon);
      }
      else if (room < 8 && !game.getLocation(seat).isRoom())
      {
        room++;
        values = getSuggestionFrom(person, room, weapon);
        person = values[0];
        room = values[1];
        weapon = values[2];
        suggestion = new Suggestion(person, room, weapon);
      }
      break;
    }
    return suggestion;
  }
  
  private int[] getSuggestionFrom(int person, int room, int weapon)
  {
    int[] values = new int[3];
    if (game.getLocation(seat).isRoom())
      room = game.getLocation(seat).getRoom();
    else
    {
      while (room < 8)
      {
        if (!hasCard(Card.ROOM, room))
          break;
        room++;
      }
    }
    while (person < 5)
    {
      if (!hasCard(Card.PERSON, person))
        break;
      person++;
    }
    while (weapon < 5)
    {
      if (!hasCard(Card.WEAPON, weapon))
        break;
      weapon++;
    }
    values[0] = person;
    values[1] = room;
    values[2] = weapon;
    return values;
  }
  
  private boolean hasCard(int type, int value)
  {
    for (Card card : knownCards)
    {
      if (card.getType() == type && card.getValue() == value)
        return true;
    }
    return false;
  }
  
}