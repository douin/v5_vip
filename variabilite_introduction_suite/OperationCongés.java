package variabilite_introduction_suite;

import operations.*;
import java.beans.*;
import java.lang.reflect.*;

public class OperationCong�s implements OperationI<Agent, R�sultat>{
    private Class<?> agentClass;
    private String   agentMethode;
    
    public void setAgentClassName(String agentClassName){
        try{
            this.agentClass = Class.forName(agentClassName);
        }catch(Exception e){
            e.printStackTrace();
        } 
    }
    public void setAgentMethodName(String agentMethode){
        this.agentMethode = agentMethode;
    }
    public void executer(Agent agent, R�sultat resultat){
        System.out.println("executer: ");
        try{
            Method m = agentClass.getDeclaredMethod(agentMethode,null);
            boolean res = (Boolean)m.invoke(agent,null);
            if(res)
              System.out.println("res: " + res);
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
 
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // public void executer(Agent agent, R�sultat r�sultat){
        // try{
            // System.out.println("BeanInfo de l'agent:");
            // BeanInfo info = Introspector.getBeanInfo( agent.getClass(), Object.class);
            // for ( PropertyDescriptor pd : info.getPropertyDescriptors() )
                // System.out.println("\tattribut: " + pd.getName() );
            // for ( MethodDescriptor md : info.getMethodDescriptors() )
                // System.out.println("\tm�thode : "+ md.getName() );
                
            // System.out.println("BeanInfo de la mairie de l'agent:");
            // info = Introspector.getBeanInfo( agent.getMairie().getClass(), Object.class);
            // for ( PropertyDescriptor pd : info.getPropertyDescriptors() )
                // System.out.println("\tattribut: " + pd.getName() );
            // for ( MethodDescriptor md : info.getMethodDescriptors() ){
                // System.out.println("\tm�thode : " + md.getName() );
                // Method m = md.getMethod();
                
            // }
            // System.out.println("BeanInfo du r�sultat:");
            // info = Introspector.getBeanInfo( r�sultat.getClass(), Object.class);
            // for ( PropertyDescriptor pd : info.getPropertyDescriptors() )
                // System.out.println("\tattribut: " + pd.getName() );
            // for ( MethodDescriptor md : info.getMethodDescriptors() )
                // System.out.println("\tm�thode : " + md.getName() );
        // }catch(Exception e){
        // }
    // }
// }
