package variabilite_introduction;

import java.util.*;
import container.Factory;
import container.ApplicationContext;
import commandes.Invocateur;
import commandes.CommandeI;
import commandes.MacroCommande;

public class CommandesAjoutéesAChaudTests extends junit.framework.TestCase{

    public void testAjoutAffichageDeDebugAvantEtAprès() throws Exception{
        ApplicationContext container = Factory.createApplicationContext("./variabilite_introduction/README.TXT");
        Agent paul = new Agent("Paul","Personne");
        paul.setAncienneté(30);
        paul.setService("01/Janvier/2006 Environnement");
        paul.setService("01/Septembre/2014 Voierie");
        System.out.println("paul:" + paul);
        ResultatEntier resultat = new ResultatEntier();

        try{
            Invocateur invocateur = container.getBean("invocateur");
            invocateur.executer(paul, resultat);
            CommandeI<Agent,ResultatEntier> debug = container.getBean("commandeDebug");
            MacroCommande<Agent,ResultatEntier> commandes = container.getBean("commandes_test");
            int tailleAvant = commandes.taille();
            commandes.ajouter(0,debug);
            commandes.ajouter(commandes.taille(),debug);
            assertEquals(tailleAvant+2, commandes.taille());
            invocateur.executer(paul, resultat);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Exception ! " + e.getMessage());
        }finally{
            System.out.println("résultat pour paul : " + resultat.getValeur());
        }
    }

    public void testAjoutDynamiqueDeLaJournéeDuMaire() throws Exception{
        ApplicationContext container = Factory.createApplicationContext("./variabilite_introduction/README.TXT");
        Agent paul = new Agent("Paul","Personne");
        paul.setAncienneté(30);
        paul.setService("01/Janvier/2006 Environnement");
        paul.setService("01/Septembre/2014 Voierie");
        paul.setMairie(new Mairie("Boulogne-Billancourt","métropole"));
        System.out.println("paul:" + paul);
        ResultatEntier resultat = new ResultatEntier();

        try{
            Invocateur invocateur = container.getBean("invocateur");
            invocateur.executer(paul, resultat);
            
            MacroCommande<Agent,ResultatEntier> commandes = container.getBean("commandes_test");
            int tailleAvant = commandes.taille();
            CommandeJoursDuMaire commande = container.getBean("commandeJoursDuMaire");
            commandes.ajouter(commandes.taille(),commande);
            assertEquals(tailleAvant+1, commandes.taille());
            invocateur.executer(paul, resultat);
            
        }catch(Exception e){
            System.out.println("Exception ! ");
        }finally{
          System.out.println("résultat pour paul : " + resultat.getValeur());
        }

    }

  
}

