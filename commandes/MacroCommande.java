package commandes;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;

import conditions.ConditionI;

public class MacroCommande<E,R> implements CommandeI<E,R>,Iterable<CommandeI<E,R>>{
    //private CommandeI<E,R>[] commandes;
    private List<CommandeI<E,R>>commandes;

    public void setCommandes(CommandeI<E,R>[] commandes){
        //this.commandes = commandes;
        this.commandes = new ArrayList<>(Arrays.asList(commandes));
    }

    @Override
    public ListIterator<CommandeI<E,R>> iterator(){
        return this.commandes.listIterator();
    }

    public boolean ajouter(int index, CommandeI<E,R> commande){
        if( (this!=commande) && !(commandes.contains(commande))){
            this.commandes.add(index, commande);
            return false;
        }
        return false;
    }

    public int taille(){return this.commandes.size();}

    @Override
    public boolean executer(final E entite,final R resultat)throws VIPException{
        for(CommandeI<E,R> cmd : commandes){
            boolean res = cmd.executer(entite,resultat);
            if(!res) return false;
        }
        return true;
    }
}

