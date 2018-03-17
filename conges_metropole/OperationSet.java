package conges_metropole;
import conges_v1.*;
import contexte.AgentI;
import operations.OperationI;

public class OperationSet implements OperationI<AgentI,ResultatConges>{
  private int operande;
  public void setOperande(final int operande){
      this.operande = operande;
  }
  
  public void executer(final AgentI agent, ResultatConges resultat){
     resultat.setJoursDeConges(operande);
  }
}
