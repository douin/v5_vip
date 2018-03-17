package variabilite_introduction_1;

import java.util.*;

import commandes.CommandeI;

public class ListeDesPromouvablesSelonGrilleIndiciaire implements CommandeI<List<Agent>,Map<Agent,Boolean>>{
    private GrilleIndiciaire grilleIndiciaire;
    
    public ListeDesPromouvablesSelonGrilleIndiciaire(){
        this.grilleIndiciaire = new GrilleIndiciaire();
    }
    
    public void setGrilleIndiciaire(GrilleIndiciaire grilleIndiciaire){
        this.grilleIndiciaire = grilleIndiciaire;
    }
    
    public boolean executer(List<Agent> liste, Map<Agent,Boolean> map){
        for(Agent agent : liste){
            boolean condition = grilleIndiciaire.indice(agent.getEchelon())>=800; // 
            map.put(agent,condition);
        }
        return true;
    }
  
}
