package variabilite_introduction;


public class Mairie{
   private String nom;
   private String lieu;

   public Mairie(String nom, String lieu){
       this.nom  = nom;
       this.lieu = lieu;
   }
   public void setNom(String nom){ this.nom = nom;}
   public String getNom(){return this.nom;}
   
   public void setRegion(String lieu){ this.lieu = lieu;}
   public String getLieu(){return this.lieu;}
   public String toString(){ return "<" + nom + "," + lieu + ">";}
   
}
