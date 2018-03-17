package variabilite_introduction_2;


import conditions.ConditionI;

public class ConditionConges implements ConditionI<Agent>{
   private static final boolean T = true;
   
   public boolean estSatisfaite(Agent agent){
      boolean condition = true;
      if(T)System.out.println("ConditionTest.estSatisfaite : " + condition);
      return condition;
   }
}
