package variabilite_introduction;

import conditions.ConditionI;

public class ConditionJoursDuMaire implements ConditionI<Agent>{
   private static final boolean T = true;
   private int    nombreDAnnéesDAnciennetéRequis;
   private String nomDeLaMairie;
   private int    nombreDeJoursSupplémentaires;
   
   public void setNombreDAnnéesDAnciennetéRequis(int nombre){
       this.nombreDAnnéesDAnciennetéRequis = nombre;
   }
   public void setNomDeLaMairie(String nom){
       this.nomDeLaMairie = nom;
   }
   public void setNombreDeJoursSupplémentaires(int nombre){
       this.nombreDeJoursSupplémentaires = nombre;
   }
   
   public int getNombreDeJoursSupplémentaires(){
     return this.nombreDeJoursSupplémentaires;
   }
   public boolean estSatisfaite(Agent agent){
      return nomDeLaMairie.equals(agent.getMairie().getNom());
   }
}