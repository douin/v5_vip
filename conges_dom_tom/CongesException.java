package conges_dom_tom;

import contexte.AgentI;
import commandes.ExceptionCommande;
import conges_v1.*;

public class CongesException extends ExceptionCommande<AgentI,ResultatConges>{
    
    @Override
    public void executer(AgentI agent, ResultatConges resultat){
        try{
            System.out.println("CongesException.execute");
        }catch(RuntimeException e){
        }
    }

}