package commandes;


/** The VIP framework: la commande
 * <R> 
 */
public interface CommandeAvecAnnulationI<E,R extends java.io.Serializable>
   extends CommandeI<E,R>{
       
    @Override  
    public boolean executer(E entite,R resultat)throws VIPException;

    public R annuler();

}
