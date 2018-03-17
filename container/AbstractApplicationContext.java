package container;


import java.util.*;
/**
 * Un conteneur de beans adapté.
 * Injection de dépendances par mutateur.
 *
 * @author jm Douin
 * @version 13 Janvier 2017
 */

public abstract class AbstractApplicationContext implements ApplicationContext, Iterable<String>{
  protected Map<String, Object> beans;
  protected String containerName;
  
  public AbstractApplicationContext(){
    this.beans = new TreeMap<String, Object>();
  }
  
  public <T> T getBean(String id){// un singleton
    Object bean = beans.get(id);
    if(bean==null) throw new RuntimeException("pas de bean avec cet identifiant: " + id);
    return (T)bean;
  }
  
  public Class<?> getType(String id){
    Object instance = getBean(id);
    if(instance==null) throw new RuntimeException("pas de bean avec cet identifiant: " + id);
    return instance.getClass();
  }
  
  public Iterator<String> iterator(){
    return new ArrayList<String>(beans.keySet()).iterator();
  }
  
  public void addApplicationContext(ApplicationContext context){
    for(String id : context){
      if(beans.get(id)!=null) throw new RuntimeException("déjà un bean avec cet identifiant: " + id);
      beans.put(id, context.getBean(id));
    }
  }
  
   public void setContainerName(String containerName){
      this.containerName = containerName;
  }
  public String getContainerName(){
      return this.containerName;
  }
}
