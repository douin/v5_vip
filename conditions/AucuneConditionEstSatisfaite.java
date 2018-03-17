package conditions;

/** Quantificateur Quelque soit.
 *    Quelque soit la condition C, C n'est pas satisfaite
 */

public class AucuneConditionEstSatisfaite<E> extends MacroCondition<E>{
 
    public boolean estSatisfaite(E e){
        for(ConditionI<E> condition : conditions){
            if(condition.estSatisfaite(e)){
                return false;
            }
        }
        return true;
    }
}
