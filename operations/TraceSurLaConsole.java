package operations;
import commandes.CommandeI;
public class TraceSurLaConsole implements CommandeI<Object,Object>{
  private boolean avecTrace = true;
  private String message="";
  public void setMessage(String message){
      this.message = message;
  }
  public void setAvecTrace(boolean avecTrace){
      this.avecTrace = avecTrace;
  }
  public boolean executer(Object o1, Object o2){
    if(avecTrace)
      System.out.println(message + "("+o1+") " + o2);
    return true;
  }
}
