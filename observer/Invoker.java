package observer;

public class Invoker<T>{
    private Command<T> command;

    public void setCommand(Command<T> command){
        this.command = command;
    }

    public <R> R execute(T t){
        return command.execute(t);
    }
}
