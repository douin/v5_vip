package interceptor;


import java.lang.reflect.*;
import java.util.*;

public class ProxyFactory{
    private Class<?> type;
    private InvocationHandler interceptor;
    
    public <T> void setType(Class<T> type){
      this.type = type;
    }
    
    public void setInterceptor(InvocationHandler interceptor){
      this.interceptor = interceptor;
    }
    
    public <T> T create(){
      Class<T> t = (Class<T>)type;
      return ProxyFactory.create(t,interceptor);
    }
   
    public static void testSyntaxe(){
      List<Integer> list = new ArrayList<Integer>();
      list = ProxyFactory.create(List.class, new Interceptor(list));
      list.add(4);
      
      List<Integer> liste = new ArrayList<Integer>();
      ProxyFactory proxy = new ProxyFactory();
      proxy.setType(List.class);
      proxy.setInterceptor(new Interceptor(liste));
      liste = proxy.create();
      int taille = liste.size();
      
    }
      
    public static <T> T create(final Class<T> type, 
                               final InvocationHandler handler){
        return type.cast(
                Proxy.newProxyInstance(type.getClassLoader(),
                new Class<?>[] {type},
                handler));
    }
}