package variabilite_introduction;

import conditions.ConditionI;

public class ConditionAnciennet� implements ConditionI<Agent>{
   private static final boolean T = true;
   private int nombreDAnn�esDAnciennet�Requis;

   public void setNombreDAnn�esDAnciennet�Requis(int nombre){
       this.nombreDAnn�esDAnciennet�Requis = nombre;
   }
    
   public boolean estSatisfaite(Agent agent){
      if(T)System.out.println("ConditionAnciennet�.estSatisfaite : " + (agent.getAnciennet�()>=nombreDAnn�esDAnciennet�Requis));
      return agent.getAnciennet�()>=nombreDAnn�esDAnciennet�Requis;
   }
}
