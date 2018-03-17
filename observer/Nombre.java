package observer;

import java.util.*;
public class Nombre extends Observable{
    private int valeur;

    public void setValeur(int valeur){
        this.valeur = valeur;
        setChanged();
        notifyObservers(this);
    }

    public int getValeur(){return valeur;}

    public void setObserver(Observer obs){
        addObserver(obs);
    }

    public boolean equals(Object o){
        return o==this; // stricte, pour les tests unitaires
    }

    public String toString(){
        return "<Nombre," + valeur + ">";
    }

}
