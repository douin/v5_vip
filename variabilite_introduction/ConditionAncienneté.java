package variabilite_introduction;

import conditions.ConditionI;

public class ConditionAncienneté implements ConditionI<Agent>{
   private static final boolean T = true;
   private int nombreDAnnéesDAnciennetéRequis;

   public void setNombreDAnnéesDAnciennetéRequis(int nombre){
       this.nombreDAnnéesDAnciennetéRequis = nombre;
   }
    
   public boolean estSatisfaite(Agent agent){
      if(T)System.out.println("ConditionAncienneté.estSatisfaite : " + (agent.getAncienneté()>=nombreDAnnéesDAnciennetéRequis));
      return agent.getAncienneté()>=nombreDAnnéesDAnciennetéRequis;
   }
}
