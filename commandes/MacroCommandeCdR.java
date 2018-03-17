package commandes;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;


import conditions.ConditionI;

public class MacroCommandeCdR<E,R> extends MaillonCdR<E,R> implements CommandeI<E,R>{

    @Override
    public boolean executer(final E entite,final R resultat)throws VIPException{
        MaillonCdR<E,R> courant = this;
        while(courant != null && this.commande != null){
            boolean res = this.commande.executer(entite, resultat);
            if(!res) return false;
            courant = courant.successeur;
            if(courant!=null) commande = courant.commande;
        }
        return true;
    }
}

