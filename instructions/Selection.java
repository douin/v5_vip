package instructions;
import commandes.CommandeI;
import conditions.ConditionI;

public class Selection<C,E,R> extends Instruction<C,E,R> {
    private ConditionI<C>      condition;
    private Instruction<C,E,R> siAlors;
    private Instruction<C,E,R> sinon;
    public void setCondition(ConditionI<C> condition){
        this.condition = condition;
    }
    
    public void setSiAlors(Instruction<C,E,R> instruction){
        this.siAlors = instruction;
    }

    public void setSinon(Instruction<C,E,R> instruction){
        this.sinon = instruction;
    }



    public boolean executer(C contexte, E entite, R resultat){
        boolean res = false;
        if(condition.estSatisfaite(contexte)){
            res = siAlors.executer(contexte, entite, resultat);
        }else{
            if(sinon != null) 
               res = sinon.executer(contexte, entite, resultat);
        }
        return res;
    }
  

    }
