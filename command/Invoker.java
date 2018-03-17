package command;


public class Invoker{
    
    private Command commande;

    public void setCommande(Command commande){
        this.commande= commande;
    }
    
    public void on(){
        commande.execute();
    }
    public void off(){
        commande.undo();
    }
}
