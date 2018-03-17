package observer;

import java.util.*;
import conditions.*;
import operations.*;

public class MacroCommandNombre implements Command<Nombre>{
    private Command<Nombre>[] commandes;
    private Condition<Nombre> condition;

    public void setCondition(Condition<Nombre> condition){
        this.condition = condition;
    }

    public void setCommandes(Command<Nombre>[] commandes){
        this.commandes = commandes;
    }

    public Nombre execute(final Nombre nombre){
        if(condition.estSatisfaite(nombre)){
            Nombre n = new Nombre();
            n.setValeur(nombre.getValeur());
            for(Command<Nombre> cmd : commandes){
                n = cmd.execute(n);
            }
            return n;
        }
        return nombre;
    }
}
