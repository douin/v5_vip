package service_locator;

import container.ApplicationContext;
import java.util.Iterator;

/** Recherche d'un bean, apparent� "service" dans 
 *  un conteneur de conteneur de beans
 */
public interface ServiceLocatorI extends Iterable<String>{
    
  /** Recherche d'un bean, � l'aide de son nom .
   * Attention en cas de doublons, le bean retourn� est le 
   * premier trouv� lors de la recherche
   * @param serviceName le nom du service
   * @return le bean
   * @throws une exception est lev�e si ce service n'existe pas
   */
  public <T> T lookup(String serviceName) throws Exception;
  
  /** Recherche d'un bean, � l'aide de son nom et du nom choisi pour son conteneur.
   * @param containerName le nom du container, ces noms doivent �tre uniques
   * @param serviceName le nom du service
   * @return le bean
   * @throws une exception est lev�e si ce service n'existe pas
   */
  public <T> T lookup(String containerName, String serviceName) throws Exception;
  
  
  /** Ajout de conteneur, 'setContainer': il faut lire 'addContainer'
   * @param container le conteneur de bean
   * @exception si l'ajout des services r�ussit (fichier inexistant...)
   */
  public void setContainer(ApplicationContext container) throws Exception;
  
  /** Retourne un it�rateur sur les services accessibles.
   * Tous les beans de tous les conteneurs ajout�s
   */
  public Iterator<String> iterator();

}