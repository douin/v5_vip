package variabilite_introduction;

import java.util.*;

import container.Factory;
import container.ApplicationContext;
import commandes.Invocateur;
import commandes.CommandeI;

public class VariabiliteTests extends junit.framework.TestCase{

    public void testPaulVoierie30Ann�esAnciennet�() throws Exception{
        ApplicationContext container = Factory.createApplicationContext("./variabilite_introduction/README.TXT");
        
        Agent paul = new Agent("Paul","Personne");
        //paul.setAnciennet�(1);
        //paul.setAnciennet�(10);
        paul.setAnciennet�(30);
        paul.setService("01/Janvier/2006 Environnement");
        paul.setService("01/Septembre/2014 Voierie");
        System.out.println("paul:" + paul);
        ResultatEntier resultat = new ResultatEntier();
        
        try{
          Invocateur invocateur = container.getBean("invocateur");
          invocateur.executer(paul, resultat);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Exception ! " + e.getMessage());
        }finally{
          System.out.println("r�sultat pour paul : " + resultat.getValeur());
        }
        
    }
    
    public void testAlfredEnvironnement10Ann�esAnciennet�() throws Exception{
        ApplicationContext container = Factory.createApplicationContext("./variabilite_introduction/README.TXT");
        
        Agent alfred = new Agent("Alfred","Personne");
        //paul.setAnciennet�(1);
        alfred.setAnciennet�(10);
        //paul.setAnciennet�(30);
        alfred.setService("01/Janvier/2006 Environnement");

        System.out.println("alfred:" + alfred);
        ResultatEntier resultat = new ResultatEntier();
        
        try{
          Invocateur invocateur = container.getBean("invocateur");
          invocateur.executer(alfred, resultat);
        }catch(Exception e){
            System.out.println("Exception ! ");
        }finally{
          System.out.println("r�sultat pour alfred : " + resultat.getValeur());
        }
        
    }
    
    
    
    
    public void ignore_testAvecCommand() throws Exception{
        ApplicationContext conteneur = Factory.createApplicationContext("./variabilite_introduction/README.TXT");
        Agent paul = new Agent("Paul","Personne");
        paul.setAnciennet�(10);
        paul.setService("  01/Janvier/2006 Environnement");
        paul.setService("01/Septembre/2014 Voierie");
        
        System.out.println("paul:" + paul);
    
        ResultatEntier resultat = new ResultatEntier();
        CommandeI<Agent,ResultatEntier> commande = conteneur.getBean("commandeAnciennet�");
        commande.executer(paul, resultat);
        
        System.out.println("r�sultat pour paul : " + resultat.getValeur());
    }
    
    
    public void testAvecUneChaineDeResponsabilit�s() throws Exception{
        ApplicationContext container = Factory.createApplicationContext("./variabilite_introduction/README.TXT");
        
        Agent alfred = new Agent("CdR","CdR");
        alfred.setAnciennet�(40);
        alfred.setService("01/Janvier/2006 Environnement");

        ResultatEntier resultat = new ResultatEntier();
        
        try{
          Invocateur invocateur = container.getBean("invocateurCdR");
          invocateur.executer(alfred, resultat);
        }catch(Exception e){
            System.out.println("Exception ! ");
        }finally{
          System.out.println("r�sultat pour alfred : " + resultat.getValeur());
        }
        
    }
    
    
    
       public void testAvecSauvegardeRestitutionDuResultat() throws Exception{
        ApplicationContext container = Factory.createApplicationContext("./variabilite_introduction/README.TXT");
        
        Agent alfred = new Agent("Sauvegarde","Restitution");
        alfred.setAnciennet�(40);
        alfred.setService("01/Janvier/2006 Environnement");

        ResultatEntier resultat = new ResultatEntier();
        
        try{
          Invocateur invocateur = container.getBean("invocateurAvecUndo");
          invocateur.executer(alfred, resultat);
          System.out.println("r�sultat pour alfred : " + resultat.getValeur());

          ResultatEntier res = (ResultatEntier)invocateur.annuler();
          System.out.println("r�sultat restitu� : " + res.getValeur());
          
        }catch(Exception e){
            System.out.println("Exception ! " + e.getMessage());
        }
              
    }
}

