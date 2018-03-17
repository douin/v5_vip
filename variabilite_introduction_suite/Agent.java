package variabilite_introduction_suite;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


public class Agent{
  private String       nom;
  private String       pr�nom;
  private int          anciennet�;
  private List<String> services; // la liste des services depuis son arriv�e
  private Mairie       mairie;
  
  public Agent(){
      this.services = new ArrayList<String>();
  }
  public Agent(String pr�nom, String nom){
      this();
      this.pr�nom   = pr�nom;
      this.nom      = nom;
      this.services = new ArrayList<String>();
  }

  public void setMairie(Mairie mairie){this.mairie = mairie;}
  public Mairie getMairie(){return this.mairie;}
  public void setNom(String nom){this.nom = nom;}
  public void setPr�nom(String prenom){this.pr�nom = pr�nom;}
  public void setAnciennet�(int anciennet�){this.anciennet� = anciennet�;} 
  public void setService(String service){
      this.services.add(service);
  }
  public String getNom(){return this.nom;}
  public String getPrenom(String pr�nom){return this.pr�nom;}
  public List<String> getServices(){
      return Collections.unmodifiableList(services);
  }
  public int getAnciennet�(){ return this.anciennet�;}
  public String toString(){
      return "<"+pr�nom+"-"+nom+","+services+">";
    }
}
