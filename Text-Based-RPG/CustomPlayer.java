
import java.util.StringTokenizer;

public class CustomPlayer extends Player {

   private static int healthPoints = 5;
      
   public CustomPlayer(String name, int room, String inventory, int health) {
      super(name, room, inventory);
      this.healthPoints = health;
   }
   
   // Add Health Point
   protected static void addHealthPoint(int healthToAdd) {
      healthPoints = healthToAdd++;
   }
   // Remove Health Point
   protected static void removeHealthPoint(int healthToRemove) {
      healthPoints = healthToRemove--;
   }
   
   // Reset Health Points
   protected static void resetHealthPoints(int resetHealth) {
      healthPoints = healthPoints;
   }
   
   // Print Health Points
   protected static String printHealthPoints() {
      String str = ("Health: " + healthPoints);
      return str;
   }
   
   // getter function to get listing of inventor
   public void printInventory() {
      System.out.println("  Health: " + healthPoints);
      StringTokenizer strT = new StringTokenizer(getInventory(), ";");
      System.out.println(getName() + "'s items: ");
      int numItems = 0;
      while (strT.hasMoreTokens()) {
         System.out.println("  " + strT.nextToken());
         numItems++;
      }
      System.out.println("  A total of " + numItems + " item(s)");
   }
}