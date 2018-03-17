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
      paul.setAncienneté(10);
      paul.setService("  01/Janvier/2006 Environnement");
      paul.setService("01/Septembre/2014 Voierie");
      
      ApplicationContext ctx = 
        Factory.createApplicationContext("./variabilite_introduction_suite/README.TXT");
      Résultat resultat = new Résultat();
      
      OperationI<Agent,Résultat> opération = ctx.getBean("operationEspagnol");
      opération.executer(paul,resultat);
        
   }
}
     // public void testAgentEnFrance() throws Exception{
        
        // Agent paul = new Agent("Paul","Personne");
        // paul.setMairie(new MairieFrance());
        // paul.setAncienneté(10);
        // paul.setService("  01/Janvier/2006 Environnement");
        // paul.setService("01/Septembre/2014 Voierie");
           
        // Résultat resultat = new Résultat();
        // Operation<Agent,Résultat> opération = new OperationCongésBonifiés();
        // opération.executer(paul,resultat);
    // }
    
    // public void testAgentEnEspagne() throws Exception{
        
        // Agent paul = new Agent("Paul","Personne");
        // paul.setMairie(new Ayuntamiento());
        // paul.setAncienneté(10);
        // paul.setService("  01/Janvier/2006 Environnement");
        // paul.setService("01/Septembre/2014 Voierie");
           
        // Résultat resultat = new Résultat();
        // Operation<Agent,Résultat> opération = new OperationCongésBonifiés();
        // opération.executer(paul,resultat);
    // }
    
    
    
    // public void testAvecInjection() throws Exception{
        // ApplicationContext ctx = Factory.createApplicationContext("./variabilite_introduction/README.TXT");
        
        // Agent paul = new Agent("Paul","Personne");
        // //paul.setAncienneté(1);
        // //paul.setAncienneté(10);
        // paul.setAncienneté(30);
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
          // System.out.println("résultat pour paul : " + resultat.getValeur());
        // }
        
    // }
    
    // public void testAvecCommand() throws Exception{
        // ApplicationContext conteneur = Factory.createApplicationContext("./variabilite_introduction/README.TXT");
        // Agent paul = new Agent("Paul","Personne");
        // paul.setAncienneté(10);
        // paul.setService("  01/Janvier/2006 Environnement");
        // paul.setService("01/Septembre/2014 Voierie");
        
        // System.out.println("paul:" + paul);
    
        // ResultatEntier resultat = new ResultatEntier();
        // Command command = conteneur.getBean("commandeAncienneté");
        // command.execute(paul, resultat);
        
        // System.out.println("résultat pour paul : " + resultat.getValeur());
    // }
// }

