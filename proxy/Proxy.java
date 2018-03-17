package proxy;


public class Proxy implements Requete{
  private Requete requete;
  private String filtre;
 
  public void setFiltre(String filtre){
      this.filtre=filtre;
  }
  public void setRequete(Requete requete){
      this.requete=requete;
  }
  
  public String executer(String url){
      if(!(url.contains(filtre))) return "500";
      return requete.executer(url);
    }
}
