package operations;

import commandes.VIPGlobalException;
import commandes.CommandeI;

public class GLOBAL_STOP<E,R> implements CommandeI<E,R>{
    private static final boolean T = true;
    
  public boolean executer(E entite, R resultat){
      if(T) System.out.println("throw new VIPGlobalException(...)");
      throw new VIPGlobalException("VIPGlobalException, entite: " + entite + ", resultat: " + resultat);
  }
}
