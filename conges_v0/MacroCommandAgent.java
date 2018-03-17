package conges_v0;

public class MacroCommandAgent extends Command<AgentI,ResultatConges>{
    private Command<AgentI,ResultatConges>[] commandes;

    public void setCommandes(Command<AgentI,ResultatConges>[] commandes){
        this.commandes = commandes;
    }

    public void execute(final AgentI agent,ResultatConges resultat){
        if(toutesLesConditionsSontSatisfaites(agent)){
            if(operation !=null)operation.executer(agent,resultat);
            try{
              for(Command<AgentI,ResultatConges> cmd : commandes){
                 cmd.execute(agent,resultat);
              }
            }catch(RuntimeException e){
                System.out.println("exception: "+e.getMessage());
                if(exception !=null)exception.executer(agent,resultat);
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
