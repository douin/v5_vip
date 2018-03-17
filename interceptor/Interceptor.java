package interceptor;


import java.lang.reflect.*;
// bean.id.5=interceptor
// interceptor.class=injection_decorateur.Interceptor
// interceptor.property.1=target
// interceptor.property.1.param.1=calculConge

// bean.id.6=proxy
// proxy.class=injection_decorateur.ProxyFactory
// proxy.property.1=type
// proxy.property.1.param.1=injection_decorateur.CongesI.class
// proxy.property.2=interceptor
// proxy.property.2.param.1=interceptor

// bean.id.7=proxy2
// proxy2.class=injection_decorateur.ProxyFactory
// proxy2.property.1=type
// proxy2.property.1.param.1=java.util.List.class
// proxy2.property.2=interceptor
// proxy2.property.2.param.1=interceptor2

// bean.id.8=interceptor2
// interceptor2.class=injection_decorateur.Interceptor
// interceptor2.property.1=target
// interceptor2.property.1.param.1=arrayList

// bean.id.9=arrayList
// arrayList.class=java.util.ArrayList
public class Interceptor implements InvocationHandler{
  private Object target;
  public Interceptor(){}
  public Interceptor(Object target){this.target = target; }
  public void setTarget(Object target){
    this.target = target;
  }
  public Object invoke(Object proxy, Method m, Object[] args)throws Throwable{
    try{
      System.out.println("interception de l'appel de " + m.getName());
      return m.invoke(target,args);
    }catch(InvocationTargetException e){
      throw e.getTargetException();
    }
  }
}