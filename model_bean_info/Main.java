package model_bean_info;

import java.beans.*;

public class Main{
    
    
  public static void main(String[] args) throws IntrospectionException{
      //BeanInfo info = Introspector.getBeanInfo( MairieFrance.class );
      BeanInfo info = Introspector.getBeanInfo( MairieFrance.class, Object.class);
      for ( PropertyDescriptor pd : info.getPropertyDescriptors() )
         System.out.println( pd.getName() );
      for ( MethodDescriptor md : info.getMethodDescriptors() )
         System.out.println( md.getName() );
  }
  
}
