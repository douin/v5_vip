package service_locator;

import container.ApplicationContext;
import container.Factory;

import contexte.MairieF;
import contexte.AgentF;
import java.util.*;
import conges_v1.*;

public class TestsAvecInjections extends junit.framework.TestCase{


    @Override
    public void setUp(){
    }
    public void testListeDesConteneurs() throws Exception{
        ApplicationContext ctx = Factory.createApplicationContext("service_locator/README.TXT");
        ServiceLocatorI serviceLocator = ctx.getBean("serviceLocator");
        System.out.println("Liste des conteneurs: ");
        System.out.println("------------------------------------------------");
        for(String container : ctx){
            System.out.print("\t" + container);
            Object obj = ctx.getBean(container);
            if(obj instanceof ApplicationContext){
               ApplicationContext app = ctx.getBean(container);
               System.out.print("\t " + app.getContainerName());
            }
            System.out.println();
        }
    }
    public void testListeDesServices() throws Exception{
        ApplicationContext ctx = Factory.createApplicationContext("./service_locator/README.TXT");
        ServiceLocatorI serviceLocator = ctx.getBean("serviceLocator");
        System.out.println("Liste des services accessibles: ");
        System.out.println("------------------------------------------------");
        for(String service : serviceLocator){
            System.out.println("\t" + service);
        }
    }
    

  
    public void testUnAgentDesDomTomAuDomTom() throws Exception{
        AgentF agent = new AgentF(){
                public MairieF getContexte(){
                    return new MairieF(){
                        public String getLieu(){ return "Sainte-Anne";}
                        public int getNombreHabitants(){ return 77;}
                        public boolean enMetropole(){return false;};
                    };
                };
                public boolean getEstTitulaire(){return true;}
                public int getAnciennete(){return 10;}
                public int getAge(){return 25;}
                public String getNom(){return "paul";}
                public int getNombreTotalDeJoursDeConges(){return 35;}
                public int getNombreDeJoursDeCongesUtilises(){return 15;}
                public boolean getEstUltraMarin(){return true;}
                public boolean getEstAnnualise(){return true;}
                public List<Couple> getObligationHebdomadaireDeService(){
                    return null;
                }
                public int getJoursDeCongesPrisEntreMaiEtOctobre(){return 1;}
                
            };
        ApplicationContext ctx = Factory.createApplicationContext("./service_locator/README.TXT");
        ServiceLocatorI serviceLocator = ctx.getBean("serviceLocator");
        ResultatConges resultat = new ResultatConges();
        commandes.Invocateur invocateur = serviceLocator.lookup("dom_tom.estimation_conges_restants");
        invocateur.executer(agent,resultat);
        System.out.println("resultat: " + resultat.getJoursDeConges());
    }
    
    public void testUnAgentDesDomTomEnMetropole() throws Exception{
        AgentF agent = new AgentF(){
                public MairieF getContexte(){
                    return new MairieF(){
                        public String getLieu(){ return "Sainte-Engrâce";}
                        public int getNombreHabitants(){ return 77;}
                        public boolean enMetropole(){return true;};
                    };
                };
                public int getAnciennete(){return 10;}
                public int getAge(){return 25;}
                public String getNom(){return "paul";}
                public boolean getEstTitulaire(){return true;}
                public int getNombreTotalDeJoursDeConges(){return 35;}
                public int getNombreDeJoursDeCongesUtilises(){return 15;}
                public boolean getEstUltraMarin(){return true;}
                public boolean getEstAnnualise(){return true;}
                public List<Couple> getObligationHebdomadaireDeService(){
                    return null;
                }
                public int getJoursDeCongesPrisEntreMaiEtOctobre(){return 1;}
 
            };
       ApplicationContext ctx = Factory.createApplicationContext("./service_locator/README.TXT");
       ServiceLocatorI serviceLocator = ctx.getBean("serviceLocator");
        System.out.println("Liste des services accessibles: ");
        System.out.println("------------------------------------------------");
        for(String service : serviceLocator){
            System.out.println("\t" + service);
        }
        ResultatConges resultat = new ResultatConges();
        commandes.Invocateur invocateur = serviceLocator.lookup("metropole.estimation_conges_restants");
        invocateur.invoquer(agent,resultat);
        System.out.println("resultat: " + resultat.getJoursDeConges());
    }
}
