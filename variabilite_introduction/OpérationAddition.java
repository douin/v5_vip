package variabilite_introduction;

import commandes.CommandeI;

public class Op�rationAddition implements CommandeI<Agent,ResultatEntier>{
  private int op�rande;
  public void setOp�rande(int nombre){
      this.op�rande = nombre;
    }
  public boolean executer(Agent agent, ResultatEntier resultat){
       resultat.setValeur(resultat.getValeur() + op�rande);
       return true;
  }

}