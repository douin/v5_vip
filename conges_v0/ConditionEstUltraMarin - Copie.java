package conges;



public class ConditionEstUltraMarin implements Condition<AgentI>{
   public boolean estSatisfaite(final AgentI agent){
       return agent.getEstUltraMarin() && agent.getContexte().enMetropole();
    }
}
