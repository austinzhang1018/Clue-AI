import java.util.*;

public class HumanView implements DisplayView
{
  private static final int UNKNOWN = -1;
  
  private Map<Card, Integer> cardMap; //maps from card to revealer (-1 if no revealer)
  private String[] playerNames;
  private String[] personNames;
  private int humanSeat;

  public HumanView()
  {
    cardMap = new TreeMap<Card, Integer>();
    for (int value = 0; value < 6; value++)
    {
      cardMap.put(new Card(Card.PERSON, value), UNKNOWN);
      cardMap.put(new Card(Card.WEAPON, value), UNKNOWN);
    }
    for (int room = 0; room < 9; room++)
      cardMap.put(new Card(Card.ROOM, room), UNKNOWN);
  }
    
  public void init(Game game, Strategy[] strategies, Suggestion solution, Card[][] allCards,
                   String[] playerNames, String[] personNames)
  {
    this.playerNames = playerNames;
    this.personNames = personNames;
    
    //find human
    humanSeat = -1;
    for (int i = 0; i < strategies.length; i++)
    {
      if (strategies[i] instanceof HumanStrategy)
        humanSeat = i;
    }

    //store human cards
    for (Card card : allCards[humanSeat])
      cardMap.put(card, humanSeat);
  }
  
  public String getText()
  {
    String text = "<html>";
    
    int prevType = Card.PERSON;
    for (Card card : cardMap.keySet())
    {
      if (card.getType() != prevType)
        text += "<br/>";
      prevType = card.getType();
      int revealer = cardMap.get(card);
      if (revealer == UNKNOWN)
        text += getCardString(card);
      else
        text += "<font color=#7f7f7f>" + getCardString(card) + " - " + playerNames[revealer] + "</font>";
      text += "<br/>";
    }
    
    text += "</html>";
    
    return text;
  }
  
  public void suggested(int suggester, Suggestion suggestion, int revealer, Card revealed)
  {
    if (suggester == humanSeat && revealed != null)
      cardMap.put(revealed, revealer);
  }
  
  private String getCardString(Card card)
  {
    if (card.isPerson())
      return personNames[card.getValue()];
    else
      return card.toString();
  }
}
