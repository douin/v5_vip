package variabilite_introduction;

import conditions.ConditionI;

public class ConditionJoursDuMaire implements ConditionI<Agent>{
   private static final boolean T = true;
   private int    nombreDAnn�esDAnciennet�Requis;
   private String nomDeLaMairie;
   private int    nombreDeJoursSuppl�mentaires;
   
   public void setNombreDAnn�esDAnciennet�Requis(int nombre){
       this.nombreDAnn�esDAnciennet�Requis = nombre;
   }
   public void setNomDeLaMairie(String nom){
       this.nomDeLaMairie = nom;
   }
   public void setNombreDeJoursSuppl�mentaires(int nombre){
       this.nombreDeJoursSuppl�mentaires = nombre;
   }
   
   public int getNombreDeJoursSuppl�mentaires(){
     return this.nombreDeJoursSuppl�mentaires;
   }
   public boolean estSatisfaite(Agent agent){
      return nomDeLaMairie.equals(agent.getMairie().getNom());
   }
}