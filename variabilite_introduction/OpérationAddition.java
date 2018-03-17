package variabilite_introduction;

import commandes.CommandeI;

public class OpérationAddition implements CommandeI<Agent,ResultatEntier>{
  private int opérande;
  public void setOpérande(int nombre){
      this.opérande = nombre;
    }
  public boolean executer(Agent agent, ResultatEntier resultat){
       resultat.setValeur(resultat.getValeur() + opérande);
       return true;
  }

}