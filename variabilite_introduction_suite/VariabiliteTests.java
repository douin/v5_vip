package variabilite_introduction_suite;

import java.util.*;

import container.*;
import operations.OperationI;
import commandes.Invocateur;
import commandes.Commande;

public class VariabiliteTests extends junit.framework.TestCase{

    public void testAgentEnEspagne() throws Exception{
      Agent paul = new AgentEspagnol();
      paul.setMairie(new Ayuntamiento());
      paul.setAnciennet�(10);
      paul.setService("  01/Janvier/2006 Environnement");
      paul.setService("01/Septembre/2014 Voierie");
      
      ApplicationContext ctx = 
        Factory.createApplicationContext("./variabilite_introduction_suite/README.TXT");
      R�sultat resultat = new R�sultat();
      
      OperationI<Agent,R�sultat> op�ration = ctx.getBean("operationEspagnol");
      op�ration.executer(paul,resultat);
        
   }
}
     // public void testAgentEnFrance() throws Exception{
        
        // Agent paul = new Agent("Paul","Personne");
        // paul.setMairie(new MairieFrance());
        // paul.setAnciennet�(10);
        // paul.setService("  01/Janvier/2006 Environnement");
        // paul.setService("01/Septembre/2014 Voierie");
           
        // R�sultat resultat = new R�sultat();
        // Operation<Agent,R�sultat> op�ration = new OperationCong�sBonifi�s();
        // op�ration.executer(paul,resultat);
    // }
    
    // public void testAgentEnEspagne() throws Exception{
        
        // Agent paul = new Agent("Paul","Personne");
        // paul.setMairie(new Ayuntamiento());
        // paul.setAnciennet�(10);
        // paul.setService("  01/Janvier/2006 Environnement");
        // paul.setService("01/Septembre/2014 Voierie");
           
        // R�sultat resultat = new R�sultat();
        // Operation<Agent,R�sultat> op�ration = new OperationCong�sBonifi�s();
        // op�ration.executer(paul,resultat);
    // }
    
    
    
    // public void testAvecInjection() throws Exception{
        // ApplicationContext ctx = Factory.createApplicationContext("./variabilite_introduction/README.TXT");
        
        // Agent paul = new Agent("Paul","Personne");
        // //paul.setAnciennet�(1);
        // //paul.setAnciennet�(10);
        // paul.setAnciennet�(30);
        // paul.setService("  01/Janvier/2006 Environnement");
        // paul.setService("01/Septembre/2014 Voierie");

        
        // System.out.println("paul:" + paul);
        
        // ResultatEntier resultat = new ResultatEntier();
        // try{
          // Invoker invoker = ctx.getBean("invoker");
          // invoker.execute(paul, resultat);
        // }catch(Exception e){
            // System.out.println("Exception ! ");
        // }finally{
          // System.out.println("r�sultat pour paul : " + resultat.getValeur());
        // }
        
    // }
    
    // public void testAvecCommand() throws Exception{
        // ApplicationContext conteneur = Factory.createApplicationContext("./variabilite_introduction/README.TXT");
        // Agent paul = new Agent("Paul","Personne");
        // paul.setAnciennet�(10);
        // paul.setService("  01/Janvier/2006 Environnement");
        // paul.setService("01/Septembre/2014 Voierie");
        
        // System.out.println("paul:" + paul);
    
        // ResultatEntier resultat = new ResultatEntier();
        // Command command = conteneur.getBean("commandeAnciennet�");
        // command.execute(paul, resultat);
        
        // System.out.println("r�sultat pour paul : " + resultat.getValeur());
    // }
// }

