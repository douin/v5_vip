package observer;

import conditions.*;

public class ConditionEstUltraMarin implements Condition<AgentI>{
   public boolean estSatisfaite(final Nombre n){
       return (n.getValeur()%2)==0;
    }
}
