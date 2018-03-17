package conges_v0;

public class Invoker<E,R>{
    private Command<E,R> command;

    public void setCommand(Command<E,R> command){
        this.command = command;
    }

    public void execute(E entite,R resultat){
        command.execute(entite,resultat);
    }
    
}
