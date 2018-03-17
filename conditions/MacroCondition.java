package conditions;

import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Arrays;

/**
 * Instruction du langage VIP : les conditions
 * si <b>Conditions(entité)</b> alors
 * @param <E> l'entité
 */
public abstract 
     class MacroCondition<E> 
              implements ConditionI<E>,Iterable<ConditionI<E>>{
    //protected ConditionI<E>[] conditions;
    protected List<ConditionI<E>> conditions;
    
    public void setConditions(ConditionI<E>[] conditions){
        //this.conditions = conditions;
        this.conditions = new ArrayList<>(Arrays.asList(conditions));
    }
    
    public ListIterator<ConditionI<E>> iterator(){
        return this.conditions.listIterator();
    }
    
    public void ajouter(int index, ConditionI<E> condition){
        this.conditions.add(index, condition);
    }
}
