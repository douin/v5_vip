package variabilite_introduction_2;

import conditions.ConditionI;

public class ConditionTantQue implements ConditionI<Memoire>{
      private static final boolean T = true;
      private String operateur;
      private String operande1;
      private int    operande2;

      public void setOperateur(String operateur){
        this.operateur = operateur;
      }
      public void setOperande1(String operande1){
        this.operande1 = operande1;
      }
      public void setOperande2(int valeur){
        this.operande2 = valeur;
      }
      
      public boolean estSatisfaite(Memoire m){
          if(T)System.out.print("ConditionTantQue:estSatisfaite: " +m);
          switch(this.operateur){
              case "<" :  return m.lire(operande1)<operande2;
              case ">" :  return m.lire(operande1)>operande2;
              case "==" : return m.lire(operande1)==operande2;
              default : return false;
          }
          
      }
    }