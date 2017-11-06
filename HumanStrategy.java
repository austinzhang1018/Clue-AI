import java.util.*;

public class HumanStrategy implements Strategy
{
  private Display display;
  private Game game;
  private int seat;
  private Card[] cards;
  
  public void setDisplay(Display display)
  {
    this.display = display;
    display.cardsHeld(seat, cards);
  }  

  public void init(Game game, int seat, Card[] cards)
  {
    this.game = game;
    this.seat = seat;
    this.cards = cards;
  }
  
  public int getActionType(boolean canStay, boolean canUseSecretPassage)
  {
    return display.getAction(canStay, canUseSecretPassage);
  }

  public Location move(int roll)
  {
    display.showDie(roll);
    
    Location dest;
    
    do
    {
      dest = display.getLocation();
    }
    while(!game.isValidMove(game.getLocation(seat), roll, dest));
    
    return dest;
  }

  public Suggestion getSuggestion()
  {
    return display.getSuggestion(game.getLocation(seat).getRoom());
  }
  
  public Card revealCard(int suggester, Suggestion suggestion)
  {
    ArrayList<Card> matches = new ArrayList<Card>();
    for (Card card : cards)
    {
      if (suggestion.contains(card))
        matches.add(card);
    }
    
    if (matches.size() == 1)
      return matches.get(0);
    else
      return display.selectCard(matches);
  }
  
  public Suggestion getAccusation()
  {
    return display.getSuggestion(-1);
  }
  
  public void suggestionMade(int suggester, Suggestion suggestion, int revealer)
  {
//    display.maybeShowMessage(display.getPlayerString(suggester) + " suggested " + suggestion);
//    display.maybeShowMessage(display.getPlayerString(revealer) + " revealed a card");
  }
  
  public void cardRevealed(Suggestion suggestion, int revealer, Card card)
  {
    //Display is updated directly by Game
//    display.cardRevealed(suggestion, revealer, card);
  }
  
  public void accusationMade(int accuser, Suggestion accusation)
  {
//    display.showMessage(display.getPlayerString(accuser) + " falsely accused " + accusation);
  }
  
  public String toString()
  {
    return "You";
  }
}
