package conges_v0;

public abstract class Command<E,R>{
    protected Condition<E>[] conditions;
    protected Operation<E,R> operation;
    protected Operation<E,R> exception;
 
    public void setConditions(Condition<E>[] conditions){
        this.conditions = conditions;
    }
    public void setOperation(Operation<E,R> operation){
        this.operation = operation;
    }
    
    public void setException(Operation<E,R> exception){
        this.exception = exception;
    }
    
    public abstract void execute(E entity,R result)throws RuntimeException;
    
}
