package instructions;


/**
 * @param <C> le contexte
 * @param <E> l'entité
 * @param <R> le résultat
 */
import commandes.CommandeI;
import conditions.ConditionI;

public class TantQue<C,E,R> extends Instruction<C,E,R> {
  private ConditionI<C>      condition;
  private Instruction<C,E,R> instruction;

  public void setCondition(final ConditionI<C> condition){
      this.condition = condition;
  }
  public void setInstruction(final Instruction<C,E,R> instruction){
      this.instruction = instruction;
  }
  public boolean executer(C contexte, E entite, R resultat){
      boolean res = true;
      while(res && condition.estSatisfaite(contexte)){
          res = instruction.executer(contexte, entite, resultat);
      }
      return res;
  }
 
}
