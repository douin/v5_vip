package commandes;

/** The VIP framework: la commande
 * @param <E> Le type de l'entité, l'objet métier, 
 * @param <R> Le type du résultat, le paramètre mutable des opérations/commandes
 */
public interface CommandeI<E,R>{
   /** Exécution d'une commande.
    * @param entite L'entité, le paramètre immutable
    * @param resultat Le résultat, le paramètre mutable des opérations/commandes
    * @return false si une exception VIPLocalException a été levée
    * @throws VIPException
    */
    public boolean executer(E entite,R resultat)throws VIPException;

}
