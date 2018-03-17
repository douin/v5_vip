package instructions;


import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.*;
import commandes.*;
import conditions.*;
import operations.*;

public class InstructionsTest{
    private static class Memoire{
        private Map<String,Integer> map;
        public Memoire(){
            map = new HashMap<>();
        }
        public int lire(String adresse){
            return map.get(adresse);
        }
        public void ecrire(String adresse, int valeur){
            map.put(adresse,valeur);
        }
        public String toString(){return map.toString();}
    }

    private static class ConditionValeurInferieure implements ConditionI<Memoire>{
        private static final boolean T = false;
        private int valeur;
        public void setValeur(int valeur){
            this.valeur = valeur;
        }
        public boolean estSatisfaite(Memoire m){
            if(T)System.out.println("estSatisfaite: " +m);
            return m.lire("index")<valeur;
        }
    }

    private static class OperationInc<E,R> extends Instruction<Memoire,E,R>{
        private static final boolean T = false;
        public boolean executer(Memoire m, E v1, R v2){
            if(T)System.out.println("OperationInc.interpreter: " + m);
            m.ecrire("index",m.lire("index")+1);
            return true;
        }
    }
    @Test
    public void testTantQue(){
        Memoire m = new Memoire();
        m.ecrire("index",0);
        TantQue<Memoire,Void,StringBuffer> tantQue = new TantQue<>();
        ConditionValeurInferieure condition = new ConditionValeurInferieure();
        condition.setValeur(10);
        tantQue.setCondition(condition);
        Instruction<Memoire,Void,StringBuffer> instruction = new OperationInc();
        tantQue.setInstruction(instruction);
        tantQue.executer(m,null,null);
        assertEquals(10,m.lire("index"));
    }
    
    @Test
    public void testSelectionEtSequence(){
        Memoire m = new Memoire();
        m.ecrire("index",2);
        Sequence<Memoire,Object,Object> seq = new Sequence<>();
        seq.setI1(new OperationInc());
        seq.setI2(new OperationInc());
        Selection<Memoire,Object,Object> sel = new Selection<>();
        ConditionValeurInferieure condition = new ConditionValeurInferieure();
        condition.setValeur(10);
        sel.setCondition(condition);
        sel.setSiAlors(seq);
        sel.setSinon(new OperationInc());
        sel.executer(m,null,null);
        assertEquals(4,m.lire("index"));
        m.ecrire("index",12);
        sel.executer(m,null,null);
        assertEquals(13,m.lire("index"));
    }
    

    
   private static class CommandeInc implements CommandeI<Void,StringBuffer>{
       public boolean executer(Void v, StringBuffer sb){
         sb.append("+1");
         return true;
        }
   }
   
   @Test
    public void testConversions(){
        Memoire m = new Memoire();
        m.ecrire("index",2);
        Sequence<Memoire,Void,StringBuffer> seq = new Sequence<>();
        seq.setI1(new OperationInc());
        seq.setI2(new OperationInc());
        StringBuffer sb = new StringBuffer("null");
        seq.executer(m, null, sb);
        assertEquals(4, m.lire("index"));
        
        InstructionEnCommande<Memoire,Void,StringBuffer> cmd = new InstructionEnCommande<Memoire, Void, StringBuffer>();
        cmd.setContexte(m);
        cmd.setInstruction(seq);
        cmd.executer(null, sb);
        assertEquals("null", sb.toString());
        CommandeEnInstruction<Memoire,Void,StringBuffer> instr = new CommandeEnInstruction<>();
        instr.setCommande(new CommandeInc());
        instr.executer(m,null,sb);
        assertEquals("null+1",sb.toString());
    }

}
