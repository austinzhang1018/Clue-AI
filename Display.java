import java.awt.*;
import java.util.*;
import javax.swing.*;

public class Display
{
  private Game game;
  private BoardComponent board;
  private JFrame frame;
  private JLabel cardLabel;
  private String[] playerNames;  //playerNames[seat number] -> player name
  private String[] personNames;  //personNames[person index] -> person name
  private DisplayView view;
  private int humanSeat;
  //private Suggestion solution;
  //private ArrayList<Suggestion>[] suggestions;
  //private Set<Card>[] cardsSeen;
  
  public Display(DisplayView view)
  {
    this.view = view;
    
    //make a window
    frame = new JFrame("Clue");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.LINE_AXIS));
    frame.getContentPane().setBackground(Color.WHITE);
    
    board = new BoardComponent(this); //creates a clue board object, which sends events to this object
    frame.getContentPane().add(board); //adds the board to the frame
    
    cardLabel = new JLabel();
    cardLabel.setPreferredSize(new Dimension(400, 400));
    cardLabel.setOpaque(true);
    cardLabel.setBackground(Color.WHITE);
    frame.getContentPane().add(cardLabel);
    
    frame.pack();
    frame.setVisible(true);
  }
  
  public void setGame(Game game, Strategy[] strategies, Suggestion solution, Card[][] allCards)
  {
    this.game = game;
    Map<Class, String> map = new HashMap<Class, String>();
    map.put(HumanStrategy.class, "You");
    
    humanSeat = -1;
    
    playerNames = new String[strategies.length];
    for (int i = 0; i < strategies.length; i++)
    {
      if (strategies[i] instanceof HumanStrategy)
      {
        if (humanSeat == -1)
          humanSeat = i;
        else
          throw new RuntimeException("Multiple human players!");
      }
      
      Class c = strategies[i].getClass();
      if (map.containsKey(c))
        playerNames[i] = map.get(strategies[i].getClass());
      else
      {
        System.out.println("no name for class " + c);
        playerNames[i] = strategies[i].toString() + "(" + i + ")";
      }
    }
    
    personNames = new String[6];
    for (int person = 0; person < 6; person++)
      personNames[person] = Card.getPersonName(person);
    for (int seat = 0; seat < game.getNumPlayers(); seat++)
      personNames[game.getPerson(seat)] = playerNames[seat];
    
    int room = -1;
    if (humanSeat == -1)
      room = solution.getRoom();
    board.setGame(game, playerNames, room);
    
//    suggestions = (ArrayList<Suggestion>[])new ArrayList[game.getNumPlayers()];
//    for (int i = 0; i < game.getNumPlayers(); i++)
//      suggestions[i] = new ArrayList<Suggestion>();
    
//    cardsSeen = (Set<Card>[])new Set[game.getNumPlayers()];
//    for (int i = 0; i < cardsSeen.length; i++)
//    {
//      cardsSeen[i] = new TreeSet<Card>();
//      for (Card card : cards[i])
//        cardsSeen[i].add(card);
//    }
    
    view.init(game, strategies, solution, allCards, playerNames, personNames);

    updateText();
  }
  
  //redraw board
  public void update()
  {
    board.showDie(-1);
  }
  
  //waits for click and returns location clicked on
  public Location getLocation()
  {
    return board.getLocationClicked();
  }
  
  public int getAction(boolean canStay, boolean canUseSecretPassage)
  {
    ArrayList<String> choices = new ArrayList<String>();
    if (canStay)
      choices.add("Stay");
    if (canUseSecretPassage)
      choices.add("Use Secret Passage");
    choices.add("Roll");
    choices.add("Accuse");
    Object[] options = new Object[choices.size()];
    for (int i = 0; i < choices.size(); i++)
      options[i] = choices.get(i);
    
    int chose = JOptionPane.showOptionDialog(frame, "What to do?", "Action",
                                             JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                                             null, options, options[0]);
    String s = choices.get(chose);
    if (s.equals("Stay"))
      return Strategy.STAY;
    else if (s.equals("Use Secret Passage"))
      return Strategy.SECRET_PASSAGE;
    else if (s.equals("Roll"))
      return Strategy.ROLL;
    else if (s.equals("Accuse"))
      return Strategy.ACCUSE;
    else
      throw new RuntimeException("invalid choice:  " + s);
  }
  
  public void showDie(int die)
  {
    board.showDie(die);
  }
  
  public Suggestion getSuggestion(int room)
  {
    return new SuggestionDialog(frame, room, personNames).getSuggestion();
  }
  
  public Card selectCard(ArrayList<Card> cards)
  {
    Object[] options = new Object[cards.size()];
    for (int i = 0; i < cards.size(); i++)
      options[i] = cards.get(i).toString();
    
    int selected = JOptionPane.showOptionDialog(frame, "Which to reveal?", "Reveal",
                                                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                                                null, options, options[0]);
    return cards.get(selected);
  }
  
  public void cardsHeld(int holder, Card[] cards)
  {
//    for (Card card : cards)
//      cardMap.put(card, holder);
    updateText();
  }
  
//  public void cardRevealed(Suggestion suggestion, int revealer, Card card)
//  {
////    if (card != null)
////    {
////      cardMap.put(card, revealer);
////      //check if only 1 unknown of this type
////      int numCards = 6;
////      if (card.getType() == Card.ROOM)
////        numCards = 9;
////      int numUnknowns = 0;
////      for (int i = 0; i < numCards; i++)
////      {
////        if (cardMap.get(new Card(card.getType(), i)) == UNKNOWN)
////          numUnknowns++;
////      }
////    }
////  
//    
////    updateText();
////    
//  }
  
  private void updateText()
  {
    cardLabel.setText(view.getText());
  }
  
  private void showMessage(String message)
  {
    JOptionPane.showMessageDialog(frame, message);
  }
  
  private String getSuggestionString(Suggestion suggestion)
  {
    return personNames[suggestion.getPerson()] +
      " in the " + Card.getRoomName(suggestion.getRoom()) +
      " with the " + Card.getWeaponName(suggestion.getWeapon());
  }
  
  private String getCardString(Card card)
  {
    if (card.isPerson())
      return personNames[card.getValue()];
    else
      return card.toString();
  }
  
  public void accused(int seat, Suggestion accusation, boolean isCorrect)
  {
    if (isCorrect)
      showMessage(playerNames[seat] + " correctly accused " + getSuggestionString(accusation));
    else
      showMessage(playerNames[seat] + " falsely accused " + getSuggestionString(accusation));
    
//    suggestions[seat].add(accusation);
//    updateText();
  }
  
  public void suggested(int suggester, Suggestion suggestion, int revealer, Card revealed)
  {
//    suggestions[suggester].add(suggestion);
//    if (revealed != null)
//      cardsSeen[suggester].add(revealed);
    
    view.suggested(suggester, suggestion, revealer, revealed);
    
    updateText();

    if (humanSeat != -1 && suggester == humanSeat)
    {
      if (revealed == null)
        showMessage("No one revealed a card");
      else
        showMessage(playerNames[revealer] + " revealed " + getCardString(revealed));
    }
  }
  
  public void onePlayerAlive(int seat)
  {
    showMessage(playerNames[seat] + " is the only player remaining");
  }
}