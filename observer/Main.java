package observer;
import container.*;


public class Main{
  public static void main(String[] args){
      ApplicationContext ctx = Factory.createApplicationContext("./observer/README.TXT");
      
      Nombre nombre = (Nombre)ctx.getBean("nombre1");
      Invoker<Nombre> invoker = (Invoker)ctx.getBean("invoker");
      Nombre res = invoker.execute(nombre);
      System.out.println("res.getValeur(): " + res.getValeur());
    }
}
