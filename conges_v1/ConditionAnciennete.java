package conges_v1;

import conditions.ConditionI;
import contexte.AgentI;

public class ConditionAnciennete implements ConditionI<AgentI>{
   private int anciennete;
   public void setAnciennete(int anciennete){
       this.anciennete = anciennete;
    }
   public boolean estSatisfaite(final AgentI agent){
       return agent.getAnciennete()> anciennete;
    }
}