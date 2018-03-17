package container;

/**
 * Un conteneur de beans.
 * Injection de dépendances par mutateur uniquement.
 *
 * @author jm Douin
 * @version  Janvier 2018
 */

public interface ApplicationContext extends Iterable<String>,java.io.Serializable{
    
  /** Retourne le nom du conteneur.
   */  
  public String getContainerName();
  
  /** Obtention d'une instance d'un bean géré par le conteneur.
   *  Il n'existe qu'une seule instance avec cet id, c'est un singleton.
   * @param id l'identifiant unique du bean
   * @return l'instance associée ou null si cet identifiant est inconnu
   */
  public <T> T getBean(String id);
  
  /** Obtention du type du bean à partir de son identifiant.
   * param id l'identifiant unique du bean
   * @return le type du bean ou null
   */
  public Class<?> getType(String id);
  
  /** Obtention d'un itérateur sur les beans déjà créés.
   * L'opération de retrait : remove, est sans effet.
   * @return un itérateur pour le parcours des identifiants du conteneur
   */
  public java.util.Iterator<String> iterator();
  
  /** Ajout, cumul d'une autre instance d'ApplicationContext.
   * Attention aux noms de beans en doublons.
   * @param appContext le conteneur à ajouter
   */
  public void addApplicationContext(ApplicationContext appContext);
}
