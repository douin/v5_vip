package conges_v1;

import operations.OperationI;
import contexte.AgentI;

public class OperationSet implements OperationI<AgentI,ResultatConges>{
  private int operande;
  public void setOperande(final int operande){
      this.operande = operande;
  }
  
  public void executer(final AgentI agent, ResultatConges resultat){
     resultat.setJoursDeConges(operande);
  }
}
