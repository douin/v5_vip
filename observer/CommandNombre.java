package observer;

import conditions.Condition;
import operations.Operation;

public class CommandNombre implements Command<Nombre>{

  private Condition<Nombre> condition;
  private Operation<Nombre> operation;
  

  public void setOperation(Operation<Nombre> operation){
      this.operation = operation;
  }
  
  public void setCondition(Condition<Nombre> condition){
      this.condition = condition;
  }
  public Nombre execute(Nombre nombre){
      assert condition!=null && operation!=null;

      if(condition.estSatisfaite(nombre)){
          return operation.execute(nombre);
      }
      return nombre;
  }
    
}
