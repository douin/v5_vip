package conges_v1;

import contexte.AgentF;
import contexte.AgentF.Couple;
import operations.OperationI;

public class CongeAnnuel implements OperationI<AgentF,ResultatConges>{

  
  public void executer(final AgentF agent, ResultatConges resultat){
      int nombreDeSemaines = 52;
      if(agent.getEstAnnualise()){
          nombreDeSemaines = 0;
          for(Couple couple : agent.getObligationHebdomadaireDeService()){
             nombreDeSemaines += couple.nombreDeSemaines;
          }
        }
      float joursDeConges = 0;
      for(Couple couple : agent.getObligationHebdomadaireDeService()){
         joursDeConges += 5*couple.nombreDeJoursTravailles * 
                          ((float)couple.nombreDeSemaines/nombreDeSemaines); 
      }
      resultat.setJoursDeConges(arrondi(joursDeConges));
  }
  
  private static float arrondi(float joursDeConges){
      int decimal = (int)(joursDeConges*10)%10;
      if(decimal>0 && decimal<5) return (Math.round(joursDeConges*2)/2F)+0.5F;
      else return (Math.round(joursDeConges*2)/2F);
      
    }
}