package commandes;

/** The VIP framework: la commande
 * @param <E> Le type de l'entit�, l'objet m�tier, 
 * @param <R> Le type du r�sultat, le param�tre mutable des op�rations/commandes
 */
public interface CommandeI<E,R>{
   /** Ex�cution d'une commande.
    * @param entite L'entit�, le param�tre immutable
    * @param resultat Le r�sultat, le param�tre mutable des op�rations/commandes
    * @return false si une exception VIPLocalException a �t� lev�e
    * @throws VIPException
    */
    public boolean executer(E entite,R resultat)throws VIPException;

}
