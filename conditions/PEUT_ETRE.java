package conditions;
import java.util.*;

public class PEUT_ETRE<E> implements ConditionI<E>{
   private static final boolean T = true;      
   private final static boolean[] res = new boolean[]{true,false};
   private static Random random = new Random();
   
   public boolean estSatisfaite(E e){
      boolean cond = res[random.nextInt(2)];
      if(T)System.out.println("condition PEUT_ETRE: " + res);
      return cond;
    }
}

