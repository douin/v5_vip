package conges_metropole;

import contexte.AgentF;
import conditions.ConditionI;

public class ConditionEstUltraMarin implements ConditionI<AgentF>{
   public boolean estSatisfaite(final AgentF agent){
       return agent.getEstUltraMarin() && agent.getContexte().enMetropole();
    }
}
