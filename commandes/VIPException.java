package commandes;



public abstract class VIPException extends RuntimeException{
  public VIPException(String message){
      super(message);
  }
  public VIPException(){
      super();
  }
}
