package proxy;

import java.util.*;
import container.*;

public class ProxyTests extends junit.framework.TestCase{

    public void testAvecInjection() throws Exception{

        ApplicationContext ctx = Factory.createApplicationContext("./proxy/README.TXT");
        Client client = (Client) ctx.getBean("client");
        System.out.print("requete en http://jfod.cnam.fr: ");
        System.out.println(client.executer("http://jfod.cnam.fr"));

        System.out.print("requete en http://www.google.fr: ");
        System.out.println(client.executer("http://www.google.fr"));

    }
    public void testSansInjection(){
        Requete reel = new Reel();
        // Proxy proxy = new Proxy(); // variabilité ici
        Proxy1 proxy = new Proxy1();
        proxy.setFiltre(".fr");
        proxy.setRequete(reel);
        Client client = new Client();
        client.setRequete(proxy);
        System.out.print("requete en http://jfod.cnam.fr: ");
        System.out.println(client.executer("http://jfod.cnam.fr"));

        System.out.print("requete en http://www.google.fr: ");
        System.out.println(client.executer("http://www.google.fr"));

    }
}

