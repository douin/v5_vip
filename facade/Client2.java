package facade;
import container.*;

// https://www.jmdoudoux.fr/java/dej/chap-design-patterns.htm
public class Client2 {

     private FacadeI facade;
    public void setFacade(FacadeI facade){
        this.facade = facade;
    }
    private static ApplicationContext ctx;
    static{
        ctx = Factory.createApplicationContext("./facade/README.TXT");
    }
    public void executer(){
        facade =(FacadeI)ctx.getBean("facade2");
        facade.methode2();
        facade.methode2();
    } 

    public static void main(String[] argv) {
        System.out.println("https://www.jmdoudoux.fr/java/dej/chap-design-patterns.htm");

        Facade2 facade = new Facade2();
        facade.methode2();
        facade.methode2();
    }
}

