public interface Strategy
{
  public static final int ACCUSE = 0;
  public static final int STAY = 1;
  public static final int ROLL = 2;
  public static final int SECRET_PASSAGE = 3;
  
  //called at start of game
  public void init(Game game, int seat, Card[] cards);
  
  //questions

  //returns ACCUSE, STAY, ROLL, or SECRET_PASSAGE
  public int getActionType(boolean canStay, boolean canUseSecretPassage);

  //returns coordinates you moved to (could be door)
  //if door, then player enters a room
  //not called if cannot move?
  public Location move(int roll);

  public Suggestion getSuggestion();  //cannot be null
  public Card revealCard(int suggester, Suggestion suggestion);
  
  //can return null if just suggested and no card revealed and now choose not to accuse
  public Suggestion getAccusation();
  
  //alerts
  
  //tells player result of another player's suggestion
  //revealer is -1 if no card revealed
  public void suggestionMade(int suggester, Suggestion suggestion, int revealer);  
  
  //tells player result of their suggestion
  //revealer is -1 and card is null if no card revealed
  public void cardRevealed(Suggestion suggestion, int revealer, Card card);
  
  public void accusationMade(int accuser, Suggestion accusation);  //someone made a bad accusation
}