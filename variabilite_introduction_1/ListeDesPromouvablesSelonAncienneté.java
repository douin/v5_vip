package variabilite_introduction_1;

import java.util.*;


import commandes.CommandeI;

public class ListeDesPromouvablesSelonAnciennet� implements CommandeI<List<Agent>,Map<Agent,Boolean>>{
    private int nombreDAnn�es;
    
    public void setNombreDAnn�es(final int nombreDAnn�es){
        this.nombreDAnn�es = nombreDAnn�es;
    }
    public boolean executer(List<Agent> liste, Map<Agent,Boolean> map){
        for(Agent agent : liste){
            boolean condition = agent.getAnciennet�()>=nombreDAnn�es;
            map.put(agent,condition);
        }
        return true;
    }
  
}
