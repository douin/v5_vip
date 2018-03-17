package conges_dom_tom;

import contexte.AgentI;
import conditions.ConditionI;

public class ConditionAnciennete implements ConditionI<AgentI>{
   private int anciennete;
   public void setAnciennete(int anciennete){
       this.anciennete = anciennete;
    }
   public boolean estSatisfaite(final AgentI agent){
       return agent.getAnciennete()> anciennete;
    }
}