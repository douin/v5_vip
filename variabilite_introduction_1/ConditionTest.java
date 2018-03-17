package variabilite_introduction_1;


import conditions.ConditionI;

public class ConditionTest implements ConditionI<Agent>{
   private static final boolean T = true;
   
   public boolean estSatisfaite(Agent agent){
      boolean condition = true;
      if(T)System.out.println("ConditionTest.estSatisfaite : " + condition);
      return condition;
   }
}
