public class Quicksort {

  public static int partition(Comparable[] a, int first, int last)
  {
    Comparable pivot = a[first];
    //Starts and Ends of Partitions
    int unknown = first + 1;
    int greater = first + 1;
    //Starts and Ends of Partitions
    int counter = 0;
    while (unknown <= last && unknown >= 0 && unknown < a.length)
    {
      //System.out.println(Main.toString(a) + "   " + first + " " + last + " " + greater + " " + unknown);
      
      if (pivot.compareTo(a[unknown]) >= 0)
      {
        
        Comparable c = a[unknown];
        a[unknown] = a[greater];
        a[greater] = c;
        greater++;
      }
      unknown++;
      counter++;
    }
    if (counter == 0)
    {return first;}

      a[first] = a[greater - 1];
      a[greater - 1] = pivot;
      return greater -1;
  }

  
  public static void sort(Comparable[] a, int first, int last)
  {
    if (last > first)
    {
      int save = partition(a, first, last);
      sort(a, first, save - 1);
      sort(a,save + 1, last);
    }
    
  }

  public static void sort(Comparable[] a)
  {
    sort(a, 0, a.length -1);
  }
}