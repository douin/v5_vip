package operations;

import commandes.*;

public class ExceptionOperation<E,R> implements CommandeI<E,R>{
  private String message;
  private VIPException vIPException;
  
  public void setMessage(final String message){
      this.message = message;
  }
  public void setVIPException(VIPException vIPException){
      this.vIPException = vIPException;
  }
  @Override
  public boolean executer(E entite, R resultat)throws VIPException{
     System.err.println("ExceptionCommand.executer, message: " + message);
     System.err.print("\t\t" + entite + "\t" + resultat);
     System.err.println("\t" + vIPException);
     if(vIPException!=null) throw vIPException;
     return true;
  }
}
