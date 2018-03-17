package proxy;

public class Client{
    private Requete requete;

    public void setRequete(Requete requete){
        this.requete = requete;
    }
    
    public String executer(String url){
        String resultat = requete.executer(url);
        // ...
        return resultat;
    }
        
}
