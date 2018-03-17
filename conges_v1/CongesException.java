package conges_v1;

import contexte.AgentI;
import commandes.ExceptionCommande;

public class CongesException extends ExceptionCommande<AgentI,ResultatConges>{
    
    @Override
    public void executer(AgentI agent, ResultatConges resultat){
        try{
            System.out.println("CongesException.execute");
        }catch(RuntimeException e){
        }
    }

}