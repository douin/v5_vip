package variabilite_introduction_suite;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


public class Agent{
  private String       nom;
  private String       prénom;
  private int          ancienneté;
  private List<String> services; // la liste des services depuis son arrivée
  private Mairie       mairie;
  
  public Agent(){
      this.services = new ArrayList<String>();
  }
  public Agent(String prénom, String nom){
      this();
      this.prénom   = prénom;
      this.nom      = nom;
      this.services = new ArrayList<String>();
  }

  public void setMairie(Mairie mairie){this.mairie = mairie;}
  public Mairie getMairie(){return this.mairie;}
  public void setNom(String nom){this.nom = nom;}
  public void setPrénom(String prenom){this.prénom = prénom;}
  public void setAncienneté(int ancienneté){this.ancienneté = ancienneté;} 
  public void setService(String service){
      this.services.add(service);
  }
  public String getNom(){return this.nom;}
  public String getPrenom(String prénom){return this.prénom;}
  public List<String> getServices(){
      return Collections.unmodifiableList(services);
  }
  public int getAncienneté(){ return this.ancienneté;}
  public String toString(){
      return "<"+prénom+"-"+nom+","+services+">";
    }
}
