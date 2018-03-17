package conges_v1;

import container.ApplicationContext;
import container.Factory;

import contexte.MairieF;
import contexte.AgentF;

import commandes.Invocateur;

import java.util.*;

public class Main{
    
    private static class Contexte implements MairieF{
        public String getLieu(){ return "Sainte-Engrâce";}
        public int getNombreHabitants(){ return 77;}
        public boolean enMetropole(){return true;}
    }

    
    public static void main(String[] args){
        ApplicationContext ctx = Factory.createApplicationContext("./conges_v1/README.TXT");
        AgentF agent = new AgentF(){
                public MairieF getContexte(){return new Contexte();}
                public int getAnciennete(){return 10;}
                public int getNombreTotalDeJoursDeConges(){return 35;}
                public int getNombreDeJoursDeCongesUtilises(){return 15;}
                public boolean getEstUltraMarin(){return true;}
                public boolean getEstTitulaire(){return true;}
                public int getAge(){return 25;}
                public String getNom(){ return "paul";}
                public List<Couple> getObligationHebdomadaireDeService(){
                   List<Couple> listeDeCouples = new ArrayList<>();
                   listeDeCouples.add(new Couple(36,4)); // 36 semaines, 4 jours travaillés
                   listeDeCouples.add(new Couple(8,2));  // 8 semaines, 2 jours travaillés
                   //listeDeCouples.add(new Couple(52,2.5f));
                   //listeDeCouples.add(new Couple(16,5));
                   return listeDeCouples;
                }
                public int getJoursDeCongesPrisEntreMaiEtOctobre(){ return 10;}
                public boolean getEstAnnualise(){return true;}
            };
        
        ResultatConges resultat = new ResultatConges();
        try{
            Invocateur<AgentF,ResultatConges> invocateur = ctx.getBean("invocateur");
            invocateur.invoquer(agent, resultat);
            
        }catch(Exception e){
            System.out.println("exception: " + e.getMessage());
        }finally{
            System.out.println("resultat: " + resultat.getJoursDeConges());
        }
    }
}
    

// #
// verbose=true

// bean.id.1=invoker
// invoker.class=commandes.Invoker
// invoker.property.1=command
// invoker.property.1.param.1=plus2

// # commande plus2 sans condition
// bean.id.8=plus2
// plus2.class=conges.CommandAgent
// plus2.property.1=operation
// plus2.property.1.param.1=operationPlus2
// plus2.property.2=conditions
// plus2.property.2.param.1=toujoursVrai

// bean.id.7=toujoursVrai
// toujoursVrai.class=conditions.VRAI

// bean.id.1=conditionEstUltraMarin
// conditionEstUltraMarin.class=conges.ConditionEstUltraMarin

// bean.id.2=conditionAnciennete
// conditionAnciennete.class=conges.ConditionAnciennete

// bean.id.3=conditionFractionnement
// conditionFractionnement.class=conges.ConditionAnciennete

// bean.id.2=operationPlus1
// operationPlus1.class=conges.OperationPlus
// operationPlus1.property.1=operande
// operationPlus1.property.1.param.1=1

// bean.id.3=operationPlus10
// operationPlus10.class=conges.OperationPlus
// operationPlus10.property.1=operande
// operationPlus10.property.1.param.1=10



// bean.id.2=toutes_les_conditions
// toutes_les_conditions.class=conditions.ToutesLesConditionsSontSatisfaites
// operationPlus1.property.1=conditions
// operationPlus1.property.1.param.1=conditionEstUltraMarin 

// bean.id.3=une_condition
// conditionEstUltraMarin.class=conges.ConditionEstUltraMarin


// bean.id.2=operationPlus1
// operationPlus1.class=conges.OperationPlus
// operationPlus1.property.1=operande
// operationPlus1.property.1.param.1=1

// bean.id.3=operationPlus10
// operationPlus10.class=conges.OperationPlus
// operationPlus10.property.1=operande
// operationPlus10.property.1.param.1=10


// # commande plus10 si ultra marin et travaillant en métropole
// bean.id.4=commandePlus10
// commandePlus10.class=conges.CommandAgent
// commandePlus10.property.1=operation
// commandePlus10.property.1.param.1=operationPlus10
// commandePlus10.property.2=conditions
// commandePlus10.property.2.param.1=conditionEstUltraMarin

// bean.id.5=macroPlus
// macroPlus.class=conges.MacroCommandAgent
// macroPlus.property.1=commandes
// macroPlus.property.1.param.1=plus plus plus plus plus
// macroPlus.property.2=conditions
// macroPlus.property.2.param.1=toujoursVrai conditionEstUltraMarin

// # commande plus 1 sans condition
// bean.id.6=plus
// plus.class=conges.CommandAgent
// plus.property.1=operation
// plus.property.1.param.1=operationPlus1
// plus.property.2=conditions
// plus.property.2.param.1=toujoursVrai

// bean.id.7=toujoursVrai
// toujoursVrai.class=conges.VRAI

// # commande plus2 sans condition
// bean.id.8=plus2
// plus2.class=conges.CommandAgent
// plus2.property.1=operation
// plus2.property.1.param.1=operationPlus2
// plus2.property.2=conditions
// plus2.property.2.param.1=toujoursVrai

// bean.id.9=operationPlus2
// operationPlus2.class=conges.OperationPlus
// operationPlus2.property.1=operande
// operationPlus2.property.1.param.1=2

// bean.id.10=macroPlusPlus
// macroPlusPlus.class=conges.MacroCommandAgent
// macroPlusPlus.property.1=commandes
// macroPlusPlus.property.1.param.1=plus2 plus2 plus2 plus100 plus plus plus plus2
// macroPlusPlus.property.2=conditions
// macroPlusPlus.property.2.param.1=toujoursVrai toujoursVrai
// macroPlusPlus.property.3=exception
// macroPlusPlus.property.3.param.1=conges_exception


// bean.id.11=invoker
// invoker.class=conges.Invoker
// invoker.property.1=command
// invoker.property.1.param.1=macroPlusPlus

// bean.id.12=operationPlus100
// operationPlus100.class=conges.OperationPlus
// operationPlus100.property.1=operande
// operationPlus100.property.1.param.1=100

// # commande plus100 sans condition
// bean.id.13=plus100
// plus100.class=conges.CommandAgent
// plus100.property.1=operation
// plus100.property.1.param.1=operationPlus100
// plus100.property.2=conditions
// plus100.property.2.param.1=toujoursVrai

// bean.id.14=conges_exception
// conges_exception.class=conges.CongesException
// conges_exception.property.1=message
// conges_exception.property.1.param.1=conges exception





