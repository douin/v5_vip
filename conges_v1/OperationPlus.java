package conges_v1;

import operations.OperationI;
import contexte.AgentI;

public class OperationPlus implements OperationI<AgentI,ResultatConges>{
  private int operande;
  public void setOperande(final int operande){
      this.operande = operande;
  }
  
  public void executer(final AgentI agent, ResultatConges resultat){
     if((agent.getNombreDeJoursDeCongesUtilises()+operande)>
         agent.getNombreTotalDeJoursDeConges())
         throw new RuntimeException("cong�s utilis�s > nombre total possible");

     resultat.setJoursDeConges(resultat.getJoursDeConges()+operande);
  }
}
