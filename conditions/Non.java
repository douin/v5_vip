package conditions;


public class Non<E> implements ConditionI<E>{
 
    protected ConditionI<E> condition;
    
    public void setCondition(ConditionI<E> condition){
        this.condition = condition;
    }

    public boolean estSatisfaite(E e){
        return !(this.condition.estSatisfaite(e));
    }
    
}
