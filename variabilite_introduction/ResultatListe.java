package variabilite_introduction;
import java.util.*;

public class ResultatListe<T> extends ResultatGénérique<List<T>>{
  private List<T> liste;
   
  public ResultatListe(){
      this.liste = new ArrayList<T>();
    }
  public void ajouter(T t){
      this.liste.add(t);
  }

  public List<T> getListe(){
      return Collections.unmodifiableList(this.liste);
  }
  
  public String toString(){
        return "ResultatListe<"+liste+">";
  }
}

