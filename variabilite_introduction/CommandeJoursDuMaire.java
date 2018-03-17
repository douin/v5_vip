package variabilite_introduction;

import conditions.ConditionI;
import commandes.CommandeI;

public class CommandeJoursDuMaire implements CommandeI<Agent,ResultatEntier>{
   private static final boolean T = true;
   private ConditionJoursDuMaire[] conditions;
   
   public void setConditions(ConditionJoursDuMaire[] conditions){
       this.conditions = conditions;
    }
   
   @Override
   public boolean executer(Agent agent, ResultatEntier resultat){
      //if(T)System.out.println("ConditionJoursDuMaire.estSatisfaite : " + condition);
      for(ConditionJoursDuMaire condition : conditions){
        boolean res = condition.estSatisfaite(agent);
        if(res) resultat.setValeur(resultat.getValeur() + condition.getNombreDeJoursSupplémentaires());
        if(T)System.out.println("ConditionJoursDuMaire.estSatisfaite : " + res);
        if (res) return false;
      }
      return true;
   }
}
