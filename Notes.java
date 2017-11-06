import java.util.*;

public class Notes
{
  private int[] persons;
  private int[] rooms;
  private int[] weapons;
  
  //0: No info
  //1: Definitely in accusation
  //2: Held by Player
  
  public Notes()
  {
    persons = new int[6];
    rooms = new int[9];
    weapons = new int[6];
    
    for(int p = 0; p < 6; p++)
    {
      persons[p] = 0;
    }
    for(int r = 0; r < 9; r++)
    {
      rooms[r] = 0;
    }
    for(int w = 0; w < 6; w++)
    {
      weapons[w] = 0;
    }
  }
  
  public int getPStatus(int personNum)
  {
    return persons[personNum];
  }
  
  public int getRStatus(int roomNum)
  {
    return rooms[roomNum];
  }
  
  public int getWStatus(int weaponNum)
  {
    return weapons[weaponNum];
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //                                                                     Not In Accusation                                                                         //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public ArrayList<Integer> getPNotInAcc()
  {
    ArrayList<Integer> notInAccPersons = new ArrayList<Integer>();
    for(int p = 0; p < 6; p++)
    {
      if(persons[p] != 1)
      {
        notInAccPersons.add(p);
      }
    }
    return notInAccPersons;
  }
  
  public ArrayList<Integer>getRNotInAcc()
  {
    ArrayList<Integer> notInAccRooms = new ArrayList<Integer>();
    for(int r = 0; r < 9; r++)
    {
      if(rooms[r] != 1)
      {
        notInAccRooms.add(r);
      }
    }
    return notInAccRooms;
  }
  
  public ArrayList<Integer> getWNotInAcc()
  {
    ArrayList<Integer> notInAccWeapons = new ArrayList<Integer>();
    for(int w = 0; w < 6; w++)
    {
      if(weapons[w] != 1)
      {
        notInAccWeapons.add(w);
      }
    }
    return notInAccWeapons;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //                                                                                No Info                                                                                  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public ArrayList<Integer> getPWithNoInfo()
  {
    ArrayList<Integer> noInfoPersons = new ArrayList<Integer>();
    for(int p = 0; p < 6; p++)
    {
      if(persons[p] == 0)
      {
        noInfoPersons.add(p);
      }
    }
    return noInfoPersons;
  }
  
  public ArrayList<Integer> getRWithNoInfo()
  {
    ArrayList<Integer> noInfoRooms = new ArrayList<Integer>();
    for(int r = 0; r < 9; r++)
    {
      if(rooms[r] == 0)
      {
        noInfoRooms.add(r);
      }
    }
    return noInfoRooms;
  }
  
  public ArrayList<Integer> getWWithNoInfo()
  {
    ArrayList<Integer> noInfoWeapons = new ArrayList<Integer>();
    for(int w = 0; w < 6; w++)
    {
      if(weapons[w] == 0)
      {
        noInfoWeapons.add(w);
      }
    }
    return noInfoWeapons;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //                                                                      Held by Player                                                                            //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
    public ArrayList<Integer> getPHeldByPlayer()
  {
    ArrayList<Integer> heldByPlayerPersons = new ArrayList<Integer>();
    for(int p = 0; p < 6; p++)
    {
      if(persons[p] == 2)
      {
        heldByPlayerPersons.add(p);
      }
    }
    return heldByPlayerPersons;
  }
  
  public ArrayList<Integer> getRHeldByPlayer()
  {
    ArrayList<Integer> heldByPlayerRooms = new ArrayList<Integer>();
    for(int r = 0; r < 9; r++)
    {
      if(rooms[r] == 2)
      {
        heldByPlayerRooms.add(r);
      }
    }
    return heldByPlayerRooms;
  }
  
  public ArrayList<Integer> getWHeldByPlayer()
  {
    ArrayList<Integer> heldByPlayerWeapons = new ArrayList<Integer>();
    for(int w = 0; w < 6; w++)
    {
      if(weapons[w] == 2)
      {
        heldByPlayerWeapons.add(w);
      }
    }
    return heldByPlayerWeapons;
  }
  
  public void changePStatus(int personNum, int newVal)
  {
    persons[personNum] = newVal;
  }
  
  public void changeRStatus(int roomNum, int newVal)
  {
    rooms[roomNum] = newVal;
  }
  
  public void changeWStatus(int weaponNum, int newVal)
  {
    weapons[weaponNum] = newVal;
  }
  
}