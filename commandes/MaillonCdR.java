package commandes;


public class MaillonCdR<E,R>{
  protected MaillonCdR<E,R> successeur;
  protected CommandeI<E,R>  commande;
  
  public void setSuccesseur(MaillonCdR successeur){
      this.successeur = successeur;
  }
  
  public void setCommande(CommandeI<E,R> commande){
      this.commande = commande;
  }
  
      
}
