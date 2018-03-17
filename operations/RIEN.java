package operations;

import commandes.CommandeI;
public class RIEN<E,R> implements CommandeI<E,R>{
    
  public boolean executer(E entite, R resultat){
      return true;
  }
}
