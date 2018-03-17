package observer;

import java.util.Observable;
import java.util.Observer;

public class NombreObserver implements Observer{
    private String nom;

    public void setNom(String nom){
        this.nom = nom;
    }

    public Observable source;

    public Observable getSource(){
        return source;
    }

    public void update(Observable src, Object o){
        System.out.println(nom + " src : " + src + " update.");
        this.source = src;
    }
}
