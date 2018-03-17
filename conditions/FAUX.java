package conditions;

public class FAUX<E> implements ConditionI<E>{
   private static final boolean T = false; //true;
   public boolean estSatisfaite(E e){
       if(T)System.out.println("condition FAUX");
       return false;
    }
}