import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class BoardComponent extends JComponent implements MouseListener
{
  //these are false locations, inside of rooms, where we want to draw players who occupy rooms
  private static final Location[] ROOM_LOCS = {
    new Location(11, 20), //ballroom
    new Location( 2, 21), //conservatory
    new Location( 2, 14), //billiard room
    new Location( 3,  8), //library
    new Location( 3,  1), //study
    new Location(11,  3), //hall
    new Location(20,  2), //lounge
    new Location(19, 12), //dining room
    new Location(20, 20)};//kitchen
  
  private static final Color[] COLORS = {
    Color.RED, Color.YELLOW, Color.WHITE, Color.GREEN, Color.BLUE, Color.MAGENTA};
  
  private Display display;
  private Game game;
  private Image boardImage;
  private Location clicked;
  private int die;
  private Image[] dieImages;
  private Image[] playerImages;
  private int solutionRoom;
  private Image bloodImage;
  private int humanSeat;
  
  public BoardComponent(Display display)
  {
    this.display = display;
    clicked = null;
    solutionRoom = -1;
    humanSeat = -1;
    
    String fileName = "clueboard.jpg";
    URL url = getClass().getResource(fileName);
    if (url == null)
      throw new RuntimeException("file not found:  " + fileName);
    boardImage = new ImageIcon(url).getImage();
    
    dieImages = new Image[7];
    for (int i = 1; i <= 6; i++)
    {
      fileName = "die" + i + ".png";
      url = getClass().getResource(fileName);
      if (url == null)
        throw new RuntimeException("file not found:  " + fileName);
      dieImages[i] = new ImageIcon(url).getImage();
    }
    
    fileName = "blood.png";
    url = getClass().getResource(fileName);
    if (url == null)
      throw new RuntimeException("file not found:  " + fileName);
    bloodImage = new ImageIcon(url).getImage();
    
    Dimension dim = new Dimension(581, 580);
    setPreferredSize(dim);
    setMinimumSize(dim);
    
    addMouseListener(this);
  }
  
  public void setGame(Game game, String[] names, int solutionRoom)
  {
    this.game = game;
    
    Map<String, String> map = new TreeMap<String, String>();
    map.put("Andy", "andy.png");
    map.put("Austin H", "austinh.png");
    map.put("Austin Z", "austinz.png");
    map.put("Clay", "clay.png");
    map.put("Eric", "eric.png");
    map.put("Jasmine", "jasmine.png");
    map.put("Jayson", "jayson.png");
    map.put("Keith", "keith.png");
    map.put("Ketan", "ketan.png");
    map.put("Nathan", "nathan.png");
    map.put("Raymond", "raymond.png");
    map.put("Shreyah", "shreyah.png");
    map.put("Tommy", "tommy.png");
    map.put("Zach", "zach.png");
    map.put("Colin", "colin.png");
    map.put("The Robot", "robot.png");
    map.put("You", "smiley.png");
    
    playerImages = new Image[names.length];
            
    for (int i = 0; i < names.length; i++)
    {
      if (map.containsKey(names[i]))
      {
        String fileName = map.get(names[i]);
        URL url = getClass().getResource(fileName);
        if (url == null)
          throw new RuntimeException("file not found:  " + fileName);
        playerImages[i] = new ImageIcon(url).getImage();
      }
      
      if (names[i].equals("You"))
        humanSeat = i;
    }
    
    this.solutionRoom = solutionRoom;
    
    repaint();
    try{Thread.sleep(100);}catch(Exception e){}
  }
  
  public void paintComponent(Graphics g) 
  {
    if (game == null)
      return;
    
    g.drawImage(boardImage, 0, 0, getWidth(), getHeight(), null);
    
    if (solutionRoom != -1)
    {
      Location loc = ROOM_LOCS[solutionRoom];
      int x = (int)(37 + loc.getX() * 21.12) - 50;
      int y = (int)(26 + loc.getY() * 21.12) - 50;
      g.drawImage(bloodImage, x, y, null);
    }
    
    //numOccupants[room] records number of occupants of room
    int[] numOccupants = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    
    ArrayList<Integer> seatOrder = new ArrayList<Integer>();
    for (int seat = 0; seat < game.getNumPlayers(); seat++)
    {
      if (seat != humanSeat)
        seatOrder.add(seat);
    }
    if (humanSeat != -1)
      seatOrder.add(humanSeat);
    
    for (Integer seat : seatOrder)
    {
      Location loc = game.getLocation(seat);
      int person = game.getPerson(seat);
      int room = -1;
      int offset = 0;
      
      if (loc.isRoom())
      {
        room = loc.getRoom();
        loc = ROOM_LOCS[room];
        offset = (numOccupants[room] - 1) * 20;
        numOccupants[room]++;
      }
      
      int x = (int)(37 + loc.getX() * 21.12) + offset - 10;
      int y = (int)(26 + loc.getY() * 21.12) - 10;
      
      if (playerImages[seat] == null)
      {
        g.setColor(COLORS[person]);
        g.fillOval(x, y, 40, 40);
        g.setColor(Color.BLACK);
        g.drawOval(x, y, 40, 40);
      }
      else
        g.drawImage(playerImages[seat], x, y, null);
    }
    
    if (die > 0)
    {
      g.drawImage(dieImages[die], (getWidth() - 112)/2, (getHeight() - 112)/2,
                  112, 112, null);
    }
  }
  
  //waits for click and returns location clicked on
  public Location getLocationClicked()
  {
    clicked = null;
    while (clicked == null)
    {
      try{Thread.sleep(100);}catch(Exception e){}
    }
    return clicked;
  }
  
  public void mouseExited(MouseEvent e)
  {
  }
  public void mouseEntered(MouseEvent e)
  {
  }
  public void mouseReleased(MouseEvent e)
  {
  }
  public void mousePressed(MouseEvent e)
  {
  }
  public void mouseClicked(MouseEvent e)
  {
    Location temp = new Location((int)((e.getX() - 37) / 21.12),
                                 (int)((e.getY() - 26) / 21.12));
    
    if (temp.getX() >= 9 && temp.getX() <= 13 &&
        temp.getY() >= 8 && temp.getY() <= 14)
    {
      //center of board
      return;
    }
    
    if (!game.isValid(temp))
    {
      //user must have clicked on a room
      int closestRoom = -1;
      int closestDist = Integer.MAX_VALUE;
      for (int room = 0; room < 9; room++)
      {
        int dx = temp.getX() - ROOM_LOCS[room].getX();
        int dy = temp.getY() - ROOM_LOCS[room].getY();
        int dist = dx * dx + dy * dy;
        if (dist < closestDist)
        {
          closestRoom = room;
          closestDist = dist;
        }
      }
      temp = new Location(closestRoom);
    }
    
    clicked = temp;
  }
  
  public void showDie(int die)
  {
    this.die = die;
    repaint();
    try{Thread.sleep(100);}catch(Exception e){}
  }
}
