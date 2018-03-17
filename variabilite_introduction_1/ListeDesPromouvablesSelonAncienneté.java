package variabilite_introduction_1;

import java.util.*;


import commandes.CommandeI;

public class ListeDesPromouvablesSelonAncienneté implements CommandeI<List<Agent>,Map<Agent,Boolean>>{
    private int nombreDAnnées;
    
    public void setNombreDAnnées(final int nombreDAnnées){
        this.nombreDAnnées = nombreDAnnées;
    }
    public boolean executer(List<Agent> liste, Map<Agent,Boolean> map){
        for(Agent agent : liste){
            boolean condition = agent.getAncienneté()>=nombreDAnnées;
            map.put(agent,condition);
        }
        return true;
    }
  
}
