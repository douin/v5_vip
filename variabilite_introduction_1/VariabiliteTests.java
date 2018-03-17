package variabilite_introduction_1;

import java.util.*;
import container.Factory;
import container.ApplicationContext;
import commandes.*;

public class VariabiliteTests extends junit.framework.TestCase{

    public void testPromouvables() throws Exception{
        ApplicationContext conteneur = Factory.createApplicationContext("./variabilite_introduction_1/README.TXT");
        Agent paul = new Agent("Paul","Personne");
        paul.setAncienneté(10);paul.setEchelon(1);
        paul.setService("  01/Janvier/2006 Environnement");
        paul.setService("01/Septembre/2014 Voierie");

        Agent bill = new Agent("Bill","Gates");
        bill.setAncienneté(4);bill.setEchelon(1);
        bill.setService("01/Septembre/2014 Voierie");

        List<Agent> liste = new ArrayList<>();
        liste.add(paul);
        liste.add(bill);

        Map<Agent,Boolean> map = new TreeMap<Agent,Boolean>();
        CommandeAvecAnnulationI invocateur = conteneur.getBean("invocateur");
        invocateur.executer(liste, map);

        for(Agent agent : map.keySet()){
            System.out.println(" agent: " + agent.getNom() + ", promouvable: " + (map.get(agent)?"oui":"non"));
        }
        invocateur.executer(liste, map);
        Map map2 = (Map)invocateur.annuler();
        System.out.println("map après restitution: " + map2);
        Map map3 = (Map)invocateur.annuler();
        System.out.println("map après restitution: " + map3);

    }

    public void testCommandeParIntrospection() throws Exception{
        try{
            ApplicationContext conteneur = Factory.createApplicationContext("./variabilite_introduction_1/README.TXT");
            Agent paul = new Agent("Paul","Personne");
            paul.setAncienneté(10);paul.setEchelon(1);
            paul.setService("  01/Janvier/2006 Environnement");
            paul.setService("01/Septembre/2014 Voierie");

            HashMap<String,Integer> res = new HashMap<>();
            res.put(paul.getNom(),0);
            CommandeI<Agent,Map<String,Integer>> commande = conteneur.getBean("commandeParIntrospection");
            commande.executer(paul, res);

            MacroCommande commandes = conteneur.getBean("deuxCommandesParIntrospection");
            commandes.executer(paul, res);
            System.out.println("Resultat, res ==  " + res);
            commandes.ajouter(0, commandes);
            commandes.executer(paul, res);
            System.out.println("Resultat, res ==  " + res);
            
            Invocateur<Agent,HashMap<String,Integer>> invocateur = conteneur.getBean("invocateurTestParIntrospection");
            invocateur.executer(paul, res);
            System.out.println("Resultat, res ==  " + res);
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}