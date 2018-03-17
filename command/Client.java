package command;

import container.*;
public class Client{

    public static void test() throws Exception{

        ApplicationContext ctx = Factory.createApplicationContext("./command/README.TXT");
        Invoker invoker = (Invoker) ctx.getBean("invoker");

        invoker.on();
        invoker.off();     
        invoker.on();
        invoker.off(); 
        invoker.on();
        invoker.off(); 
    }

}
