package observer;


public interface Command<T>{
  
    public <R> R execute(T t);
}
