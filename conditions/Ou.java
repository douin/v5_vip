package conditions;


public class Ou<E> implements ConditionI<E>{
 
    protected ConditionI<E> cond1;
    protected ConditionI<E> cond2;
    
    public void setCond1(ConditionI<E> condition){
        this.cond1 = condition;
    }
    public void setCond2(ConditionI<E> condition){
        this.cond2 = condition;
    }

    public boolean estSatisfaite(E e){
        return (this.cond1.estSatisfaite(e) | this.cond2.estSatisfaite(e));
    }
    
}