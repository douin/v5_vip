package service_locator;

import container.ApplicationContext;
import java.util.Iterator;

/** Recherche d'un bean, apparenté "service" dans 
 *  un conteneur de conteneur de beans
 */
public interface ServiceLocatorI extends Iterable<String>{
    
  /** Recherche d'un bean, à l'aide de son nom .
   * Attention en cas de doublons, le bean retourné est le 
   * premier trouvé lors de la recherche
   * @param serviceName le nom du service
   * @return le bean
   * @throws une exception est levée si ce service n'existe pas
   */
  public <T> T lookup(String serviceName) throws Exception;
  
  /** Recherche d'un bean, à l'aide de son nom et du nom choisi pour son conteneur.
   * @param containerName le nom du container, ces noms doivent être uniques
   * @param serviceName le nom du service
   * @return le bean
   * @throws une exception est levée si ce service n'existe pas
   */
  public <T> T lookup(String containerName, String serviceName) throws Exception;
  
  
  /** Ajout de conteneur, 'setContainer': il faut lire 'addContainer'
   * @param container le conteneur de bean
   * @exception si l'ajout des services réussit (fichier inexistant...)
   */
  public void setContainer(ApplicationContext container) throws Exception;
  
  /** Retourne un itérateur sur les services accessibles.
   * Tous les beans de tous les conteneurs ajoutés
   */
  public Iterator<String> iterator();

}