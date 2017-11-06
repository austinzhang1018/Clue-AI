public interface DisplayView
{
  //allCards[seat] -> cards held by that seat
  //playerNames[seat] -> name
  //personNames[person] -> name
  void init(Game game, Strategy[] strategies, Suggestion solution, Card[][] allCards,
            String[] playerNames, String[] personNames);
  
  String getText();
  
  void suggested(int suggester, Suggestion suggestion, int revealer, Card revealed);
}
