package commandes;

import java.lang.reflect.*;

import conditions.ConditionI;
import commandes.CommandeI;

public class CommandeParIntrospection<E,R> implements CommandeI<E,R>{
    protected ConditionI<?>   instanceCondition=null;
    protected CommandeI<?,?>  instanceOperation=null;
    protected CommandeI<?,?>  instanceException=null;
    
    public void setCondition(String condition){
        try{
            Class<?> classCondition = Class.forName(condition);
            instanceCondition = (ConditionI<?>)classCondition.newInstance();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setOperation(String operation){
        try{
            Class<?> classOperation = Class.forName(operation);
            instanceOperation = (CommandeI<?,?>) classOperation.newInstance();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setException(String exception){
        try{
            Class<?> classException = Class.forName(exception);
            instanceException = (CommandeI<?,?>) classException.newInstance();
         }catch(Exception e){
            e.printStackTrace();
        }
    }

    public boolean executer(E entite,R resultat)throws VIPException{
        Method mCondition=null, mOperation=null, mException=null;
        try{
            if(instanceCondition!=null)
                mCondition = instanceCondition.getClass().getDeclaredMethod("estSatisfaite",entite.getClass());
            if(instanceOperation!=null)
                mOperation = instanceOperation.getClass().getDeclaredMethod("executer",entite.getClass(), resultat.getClass());
            if(instanceException!=null)
                mException = instanceException.getClass().getDeclaredMethod("executer",entite.getClass(), resultat.getClass());

            // condition.estSatisfaite(entite)
            boolean condition = instanceCondition!=null;
            condition &= (boolean)mCondition.invoke(instanceCondition,entite);
            if(condition || instanceCondition==null){
                //operation.executer(entite,resultat);
                return (Boolean)mOperation.invoke(instanceOperation,entite,resultat);
            }
        }catch(VIPLocalException e){
            return false;
        }catch(VIPGlobalException e){
            throw new VIPGlobalException(e.getMessage());
        }catch(NoSuchMethodException e){
            e.printStackTrace();
            throw new VIPGlobalException(e.getMessage());
        }catch(IllegalAccessException e){
            e.printStackTrace();
            throw new VIPGlobalException(e.getMessage());
        }catch(InvocationTargetException e){
            e.printStackTrace();
            throw new VIPGlobalException(e.getMessage());
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }finally{
            if(instanceException!=null)
              //return exception.executer(entité,resultat);
              try{
                  return (Boolean)mException.invoke(instanceException,entite,resultat);
              }catch(Exception e){
                  e.printStackTrace();
                  throw new VIPGlobalException(e.getMessage());
              }
        }

        return true;
    }

}
