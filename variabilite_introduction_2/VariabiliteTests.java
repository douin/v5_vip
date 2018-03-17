package variabilite_introduction_2;

import java.util.*;
import container.Factory;
import container.ApplicationContext;
import commandes.*;
import conditions.ConditionI;
import instructions.Instruction;
import instructions.TantQue;
import instructions.Selection;
import instructions.Sequence;
import instructions.CommandeEnInstruction;

public class VariabiliteTests extends junit.framework.TestCase{

    public void testTantQueAvecInjection(){
        ApplicationContext conteneur = Factory.createApplicationContext("./variabilite_introduction_2/README.TXT");
        
        Agent agent = new Agent("a","a");
        agent.setMairie(new Mairie("Boulogne"));
        ResultatConges resultat = new ResultatConges();
        Memoire m = new Memoire();
        
        Instruction exec = conteneur.getBean("exec");
        exec.executer(m, agent, resultat);
  
    }
    
    public void testTantQueSansInjection(){
        
        TantQue<Memoire,Agent,ResultatConges> tantQue = new TantQue<>();
        ConditionTantQue condition = new ConditionTantQue();
        condition.setOperateur("<");
        condition.setOperande1("index");
        condition.setOperande2(10);
        tantQue.setCondition(condition);
        
        Instruction<Memoire,Agent,ResultatConges> inc = new InstructionInc();
        Sequence<Memoire,Agent,ResultatConges> sequence = new Sequence<>();
        sequence.setI1(inc);
        
        CommandeEnInstruction<Memoire,Agent,ResultatConges> cmdInstr = new CommandeEnInstruction();
        cmdInstr.setCommande(new CommandeConges());
        sequence.setI2(cmdInstr);
        
        Selection<Memoire,Agent,ResultatConges> selection = new Selection<>();
        selection.setCondition(new ConditionEstPair());
        selection.setSiAlors(sequence);
        selection.setSinon(inc);
        
        tantQue.setInstruction(selection);
        
        Memoire m = new Memoire();
        Agent agent = new Agent("a","a");
        agent.setMairie(new Mairie("Boulogne"));
        ResultatConges resultat = new ResultatConges();
        
        inc.executer(m,agent,resultat);
        selection.executer(m,agent,resultat);

        tantQue.executer(m,agent,resultat);
    }
    /* while(index<10)
     *   if(estPair(index))index++
     *   else index++
     *   
     */
    public void testTantQueSimpleSansInjection(){
        Memoire m = new Memoire();
        TantQue<Memoire,List<Agent>,Map<Agent,Integer>> tantQue = new TantQue<>();
        ConditionTantQue condition = new ConditionTantQue();
        condition.setOperateur("<");
        condition.setOperande1("index");
        condition.setOperande2(10);
        tantQue.setCondition(condition);
        
        Instruction<Memoire,List<Agent>,Map<Agent,Integer>> inc = new InstructionInc();
        Selection<Memoire,List<Agent>,Map<Agent,Integer>> selection = new Selection();
        selection.setCondition(new ConditionEstPair());
        selection.setSiAlors(inc);
        selection.setSinon(inc);
        Instruction<Memoire,List<Agent>,Map<Agent,Integer>> instruction = selection;
        tantQue.setInstruction(selection);
        List<Agent> listeAgents = new ArrayList<>();
        Map<Agent,Integer> resultat = new HashMap<>();
  
        tantQue.executer(m,listeAgents,resultat);
    }
    
    public void testTantQueSansInjectionBis(){
        Memoire m = new Memoire();
        TantQue<Memoire,Object,Object> tantQue = new TantQue();
        ConditionTantQue condition = new ConditionTantQue();
        condition.setOperateur("<");
        condition.setOperande1("index");
        condition.setOperande2(10);
        tantQue.setCondition(condition);
        Instruction<Memoire,Object,Object> instruction = new InstructionInc();
        tantQue.setInstruction(instruction);
        tantQue.executer(m,null,null);
    }
    
   
}