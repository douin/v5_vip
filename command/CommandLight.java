package command;


public class CommandLight implements Command{
    // variables d'instance - remplacez l'exemple qui suit par le vôtre
    private Light lampe;

    public void setLampe(Light lampe){
        this.lampe = lampe;
    }

    public CommandLight(){
    }

   public void execute(){
       lampe.turnOn();
    }
   public void undo(){
       lampe.turnOff();
    }
       
}
