package strategy;

import container.Factory;
import container.ApplicationContext;

public class StrategyPatternDemoWithFemtoContainer {
   public static void main(String[] args) throws Exception{
      ApplicationContext ctx = Factory.createApplicationContext("./strategy/README.TXT");
      Context context = (Context) ctx.getBean("context1");
      System.out.println("10 + 5 = " + context.executeStrategy(10, 5));

      context = (Context) ctx.getBean("context2");
      System.out.println("10 + 5 = " + context.executeStrategy(10, 5));
 	
      context = (Context) ctx.getBean("context3");
      System.out.println("10 * 5 = " + context.executeStrategy(10, 5));
   }
}

