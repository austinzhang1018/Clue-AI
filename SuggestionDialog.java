import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SuggestionDialog extends JDialog implements ActionListener
{
  private Suggestion suggestion;
  private int room;
  private JComboBox personBox;
  private JComboBox roomBox;
  private JComboBox weaponBox;
  
  public SuggestionDialog(JFrame frame, int room, String[] personNames)
  {
    super(frame, true);
    this.room = room;
    
    if (room == -1)
      setTitle("Accusation");
    else
      setTitle("Suggestion");
    
    getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
    
    if (room == -1)
      getContentPane().add(new JLabel("I accuse"));
    else
      getContentPane().add(new JLabel("I suggest it was"));
    
    personBox = new JComboBox(personNames);
    getContentPane().add(personBox);
    
    if (room == -1)
    {
      getContentPane().add(new JLabel("in the"));
      String[] roomNames = new String[9];
      for (int i = 0; i < 9; i++)
        roomNames[i] = Card.getRoomName(i);
      roomBox = new JComboBox(roomNames);
      getContentPane().add(roomBox);
    }
    else
    {
      getContentPane().add(new JLabel("in the " + Card.getRoomName(room)));
    }
    
    getContentPane().add(new JLabel("with the"));
    
    String[] weaponNames = new String[6];
    for (int i = 0; i < 6; i++)
      weaponNames[i] = Card.getWeaponName(i);
    weaponBox = new JComboBox(weaponNames);
    getContentPane().add(weaponBox);

    JButton button = new JButton("OK");
    getContentPane().add(button);
    button.addActionListener(this);
    
    //center components within dialog (horizontally)
    for (Component component : getContentPane().getComponents())
      ((JComponent)component).setAlignmentX(CENTER_ALIGNMENT);
    
    pack();
    
    //center dialog over frame
    setLocation((frame.getWidth() - getWidth()) / 2,
                (frame.getHeight() - getHeight()) / 2);
  }
  
  public Suggestion getSuggestion()
  {
    setVisible(true);
    return suggestion;
  }
  
  public void actionPerformed(ActionEvent e)
  {
    int person = personBox.getSelectedIndex();
    if (room == -1)
      room = roomBox.getSelectedIndex();
    int weapon = weaponBox.getSelectedIndex();
    suggestion = new Suggestion(person, room, weapon);
    setVisible(false);
  }
}