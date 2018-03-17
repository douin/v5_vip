package commandes;
import java.util.Stack;
import java.io.*;

public class Invocateur<E,R extends java.io.Serializable> 
                 implements CommandeAvecAnnulationI<E,R>{
                     
    private CommandeI<E,R> commande;
    private CommandeI<E,R> exception;
    private boolean        avecSauvegarde;

    private Stack<byte[]> sauvegarde; // stack plusieurs appels imbriqués ? possible ?

    public Invocateur(){
        
    }

    public void setCommande(CommandeI<E,R> commande){
        this.commande = commande;
    }

    public void setException(CommandeI<E,R> exception){
        this.exception = exception;
    }

    public void setAvecSauvegarde(final boolean avecSauvegarde){
        this.avecSauvegarde = avecSauvegarde;
        if(this.avecSauvegarde) this.sauvegarde = new Stack<byte[]>();
    }

    @Override
    public boolean executer(E entite,R resultat)throws VIPException{
        if(avecSauvegarde)
          sauvegarderResultat(resultat); 
        return commande.executer(entite,resultat);
    }

    public R annuler(){
        R res = null;
        if(avecSauvegarde)
            return restituterResultat();
            
        return res;
    }

    private void sauvegarderResultat(Serializable resultat){
        if(resultat!=null){
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try{
                ObjectOutput out = new ObjectOutputStream(bos);
                out.writeObject(resultat);
                sauvegarde.push(bos.toByteArray());
                bos.close();
                out.close();
            }catch(Exception e){
                //throw new RuntimeException("sauvegarde: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private R restituterResultat(){
        R resultat = null;
        try{
            if(!sauvegarde.isEmpty()){
                byte[] contenu = sauvegarde.pop();
                ByteArrayInputStream bis = new ByteArrayInputStream(contenu);
                ObjectInput in = new ObjectInputStream(bis);
                resultat = (R) in.readObject();
                bis.close();
                in.close();
            }
        }catch(Exception e){
            //throw new RuntimeException("restitution: " + e.getMessage());
            e.printStackTrace();
        }
        return resultat;

    }

}
