package variabilite_introduction_2;

import java.util.*;
import commandes.CommandeI;

public class CommandeConges implements CommandeI<Agent,ResultatConges>{
  @Override
  public boolean executer(Agent agent,ResultatConges res){
       res.inc();res.inc();
       System.out.println("CommandeConges: resultat: " + res);
       return true;
    }

}