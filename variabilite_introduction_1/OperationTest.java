package variabilite_introduction_1;

import java.util.*;
import commandes.CommandeI;

public class OperationTest implements CommandeI<Agent,HashMap<String,Integer>>{
  @Override
  public boolean executer(Agent agent,HashMap<String,Integer> map){
       map.put(agent.getNom(),map.get(agent.getNom())+1);
       System.out.println("OperationTest agent: " + agent + " resultat: " + map);
       return true;
    }

}