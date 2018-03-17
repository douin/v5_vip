package variabilite_introduction_1;


import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Agent implements Comparable<Agent>, java.io.Serializable{
  private String       nom;
  private String       pr�nom;
  private int          anciennet�;
  private Mairie       mairie;
  private List<String> services; // la liste des services depuis son arriv�e
  private int echelon;
  
  
  public Agent(String pr�nom, String nom){
      this.pr�nom   = pr�nom;
      this.nom      = nom;
      this.services = new ArrayList<String>();
  }

  public int compareTo(Agent a){
      return nom.compareTo(a.nom);
    }
  public void setNom(String nom){this.nom = nom;}
  public void setPr�nom(String prenom){this.pr�nom = pr�nom;}
  public void setAnciennet�(int anciennet�){this.anciennet� = anciennet�;} 
  public void setEchelon(int echelon){this.echelon = echelon;}
  public void setService(String service){
      this.services.add(service);
  }
  public void setMairie(Mairie mairie){ this.mairie = mairie;}
  public String getNom(){return this.nom;}
  public String getPrenom(String pr�nom){return this.pr�nom;}
  public int getEchelon(){return this.echelon;}
  public List<String> getServices(){
      return Collections.unmodifiableList(services);
  }
  public int getAnciennet�(){ return this.anciennet�;}
  public Mairie getMairie(){ return mairie;}
  public String toString(){
      return "<"+pr�nom+"-"+nom+","+mairie+","+services+">";
  }
  public boolean equals(Object o){
      if(!(o instanceof Agent)) return false;
      Agent a = (Agent)o;
      return nom.equals(a.nom);
  }
  public int hashCode(){return nom.hashCode();}
}
