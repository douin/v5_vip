package observer;

import conditions.*;

public class ConditionEstPair implements Condition<Nombre>{
   public boolean estSatisfaite(final Nombre n){
       return (n.getValeur()%2)==0;
    }
}
