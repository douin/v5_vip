package instructions;
import commandes.CommandeI;
/** Le patron Adaptateur...
 */
public class CommandeEnInstruction<C,E,R> extends Instruction<C,E,R> {
  protected CommandeI<E,R> commande;
  public void setCommande(CommandeI<E,R> commande){
      this.commande = commande;
  }

  public boolean executer(C contexte,E entite, R resultat){
     return commande.executer(entite,resultat);
  }
  
}