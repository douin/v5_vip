package variabilite_introduction;

public class ResultatEntier extends ResultatG�n�rique<Integer>{
   private int valeur;
  
    public Integer getValeur(){
        return this.valeur;
    }
        
    public void setValeur(Integer i){
        this.valeur = i.intValue();
    }
  
    public String toString(){
        return "Resultat<"+valeur+">";
    }
}