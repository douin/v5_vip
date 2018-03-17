package conditions;

/** Quantificateur Il existe.
 *    Il existe une condition C tel que C est satisfaite
 */
public class UneDesConditionsEstSatisfaite<E> extends MacroCondition<E>{

  public boolean estSatisfaite(E e){
      for(ConditionI<E> condition : conditions){
          if(condition.estSatisfaite(e))return true;
      }
      return false;
    }
}
