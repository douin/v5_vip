package variabilite_introduction_2;

import conditions.ConditionI;

public class ConditionEstPair implements ConditionI<Memoire>{
    private static final boolean T = true;
      private String adresse;

      public void setAdresse(String adresse){
        this.adresse = adresse;
      }

      public boolean estSatisfaite(Memoire m){
          if(T)System.out.println("ConditionEstPair:estSatisfaite: " +m + " " + ((m.lire(adresse)%2)==0));
          return (m.lire(adresse)%2)==0;
      }
    }