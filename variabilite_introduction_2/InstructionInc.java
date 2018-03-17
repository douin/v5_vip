package variabilite_introduction_2;

import instructions.Instruction;

public class InstructionInc<E,R> extends Instruction<Memoire,E,R>{
      private static final boolean T = true;
      
      public boolean executer(Memoire m, E e, R r){
          if(T)System.out.println("InstructionInc.executer: " + m);
          m.ecrire("index",m.lire("index")+1);
          return true;
      }
    }