package commandes;

import conditions.ConditionI;

public class Commande<E,R> implements CommandeI<E,R>{
    protected ConditionI<E>  condition;
    protected CommandeI<E,R> operation;
    protected CommandeI<E,R> exception;

    public void setCondition(ConditionI<E> condition){
        this.condition = condition;
    }

    public void setOperation(CommandeI<E,R> operation){
        this.operation = operation;
    }

    public void setException(CommandeI<E,R> exception){
        this.exception = exception;
    }

    public boolean executer(E entité,R resultat)throws VIPException{
       
        if((condition!=null && condition.estSatisfaite(entité)) || 
           (condition==null)){
            try{
                return operation.executer(entité,resultat);
            }catch(VIPLocalException e){
                return false;
            }catch(VIPGlobalException e){
                 throw new VIPGlobalException(e.getMessage());
            }catch(StackOverflowError e){
                 throw new RuntimeException(e.getMessage());
            }catch(Exception e){
              e.printStackTrace();
              throw e;
            }finally{
               if(exception!=null)return exception.executer(entité,resultat);
            }
        }
        return true;
    }

}
