package interceptor;


import java.util.*;

import container.*;
import commandes.Invoker;
import commandes.Command;

public class ProxyFactoryTests extends junit.framework.TestCase{

    public void testNouvelleMairie() throws Exception{
      ApplicationContext ctx = Factory.createApplicationContext("./interceptor/README.TXT");

      ProxyFactory proxy = ctx.getBean("proxy1");
      MairieI mairie = proxy.create();
      int nombre = mairie.nombreDHabitants();
      ((MairieE)mairie).specifique();

    
    }
      
    public void testSansInjection() throws Exception{
      MairieI ceuta = new MairieE();
      ceuta = ProxyFactory.create(MairieI.class, new Interceptor(ceuta));
      int nombreCeuta = ceuta.nombreDHabitants();
      
      MairieI melilla = new MairieE();
      ProxyFactory proxy = new ProxyFactory();
      proxy.setType(MairieI.class);
      proxy.setInterceptor(new Interceptor(melilla));
      melilla = proxy.create();
      int nombre = melilla.nombreDHabitants();
    }
  
}

