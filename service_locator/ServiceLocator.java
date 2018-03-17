package service_locator;

import java.util.*;
import container.*;

public class ServiceLocator implements ServiceLocatorI{
    // une clef pour un accès direct au conteneur, utile/inutile ? à voir
    // 
    private Map<String,ApplicationContext> containers;
    //private Map<String,Object> cache; 

    public ServiceLocator(){
        this.containers = new TreeMap<String,ApplicationContext>();
        //this.cache = new HashMap<String,Object>();
    }

    public <T> T lookup(String serviceName) throws Exception{
        for(String app_key : containers.keySet()){
            ApplicationContext container = containers.get(app_key);
            for( String bean : container){
                if(serviceName.equals(bean)){ 
                    Object service = container.getBean(bean);
                    return (T)service;
                }
            }
        }
        throw new Exception(" service not found:" + serviceName);
    }

    public <T> T lookup(String containerName, String serviceName) throws Exception{
        ApplicationContext container = containers.get(containerName);
        if(container!=null){
            Object service = container.getBean(serviceName);
            return (T)service;
        }else{
            throw new Exception("container not found: " + containerName);
        }
    }

    /** Ajout d'un conteneur de beans
     * @param container
     */
    public void setContainer(ApplicationContext container) throws Exception{
        try{
            containers.put(container.getContainerName(),container);
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    private List<String> services(){
        List<String> services = new ArrayList<String>();
        for(String prefix : containers.keySet()){
            ApplicationContext container = containers.get(prefix);
            for(String bean : container)
                services.add(bean);
        }
        //Collections.sort(services);
        return services;
    }

    public Iterator<String> iterator(){
        return services().iterator();
    }
}

