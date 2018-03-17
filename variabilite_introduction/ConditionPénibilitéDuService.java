package variabilite_introduction;

import conditions.ConditionI;
import java.util.List;

public class ConditionPénibilitéDuService implements ConditionI<Agent>{
   private static final boolean T = true;
   private String service;

   public void setService(String service){
       this.service = service;
   }
    
   public boolean estSatisfaite(Agent agent){
      List<String> services = agent.getServices();
      if(services.size()>0){
          String serviceActuel = services.get(services.size()-1);
          if(T)System.out.println("ConditionPénibilité.estSatisfaite : " + (serviceActuel.contains(service)));
          return serviceActuel.contains(service);
      }
      else
        throw new RuntimeException("l'agent " + agent + " est-il sans affectation ???");
   }
}