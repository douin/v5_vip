package conges_dom_tom;

import contexte.AgentI;
import operations.OperationI;
import conges_v1.*;

public class OperationSet implements OperationI<AgentI,ResultatConges>{
  private int operande;
  public void setOperande(final int operande){
      this.operande = operande;
  }
  @Override
  public void executer(final AgentI agent, ResultatConges resultat){
     resultat.setJoursDeConges(operande);
  }
}
