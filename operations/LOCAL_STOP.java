package operations;

import commandes.VIPLocalException;
import commandes.CommandeI;
public class LOCAL_STOP<E,R> implements CommandeI<E,R>{
   private static final boolean T = true;
    
  public boolean executer(E entite, R resultat){
      if(T) System.out.println("throw new VIPLocalException(...)");
      throw new VIPLocalException("VIPLocalException, entite: " + entite + ", resultat: " + resultat);
  }
}