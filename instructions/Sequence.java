package instructions;

import commandes.CommandeI;

public class Sequence<C,E,R> extends Instruction<C,E,R> {
    private Instruction<C,E,R> i1;
    private Instruction<C,E,R> i2;

    public void setI1(Instruction<C,E,R> instruction){
        this.i1 = instruction;
    }
    public void setI2(Instruction<C,E,R> instruction){
        this.i2 = instruction;
    }
    
    public boolean executer(C contexte, E entite, R resultat){
        boolean res = i1.executer(contexte, entite, resultat);
        if(res && i2!=null)
          res = i2.executer(contexte, entite, resultat);
          
        return res;
    }

}
