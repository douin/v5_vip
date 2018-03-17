package conges_dom_tom;

import contexte.AgentI;
import conges_v1.*;
import operations.OperationI;

public class OperationPlus implements OperationI<AgentI,ResultatConges>{
  private int operande;
  public void setOperande(final int operande){
      this.operande = operande;
  }
  
  public void executer(final AgentI agent, ResultatConges resultat){
     if((agent.getNombreDeJoursDeCongesUtilises()+operande)>
         agent.getNombreTotalDeJoursDeConges())
         throw new RuntimeException("congés utilisés > nombre total possible");

     resultat.setJoursDeConges(resultat.getJoursDeConges()+operande);
  }
}
