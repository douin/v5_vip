package variabilite_introduction_2;

import java.util.*;
public class Memoire{
  private Map<String,Integer> map;
  public Memoire(){
      map = new HashMap<>();
  }
  
  public int lire(String adresse){
      if(map.get(adresse)==null)map.put(adresse,0);
      return map.get(adresse);
  }
    
  public void ecrire(String adresse, int valeur){
      map.put(adresse,valeur);
  }
  
  public String toString(){
      return map.toString();
    }
}
