package conges_v0;


public class CommandAgent extends Command<AgentI,ResultatConges>{
    
  public void execute(AgentI agent, ResultatConges resultat){

      if(toutesLesConditionsSontSatisfaites(agent)){
        try{
            operation.executer(agent, resultat);
        }catch(RuntimeException e){
            if(exception!=null)exception.executer(agent,resultat);
            throw e;
        }
      }
  }
  private boolean toutesLesConditionsSontSatisfaites(AgentI agent){
        for(Condition<AgentI> condition : conditions){
            if(!condition.estSatisfaite(agent))return false;
        }
        return true;
    }
  
 
}
