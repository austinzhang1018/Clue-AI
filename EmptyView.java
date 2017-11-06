public class EmptyView implements DisplayView
{
  public void init(Game game, Strategy[] strategies, Suggestion solution, Card[][] allCards,
                   String[] playerNames, String[] personNames)
  {
  }
  
  public String getText()
  {
    return "";
  }
  
  public void suggested(int suggester, Suggestion suggestion, int revealer, Card revealed)
  {
  }
}