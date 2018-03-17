package model_mbean;

import container.*;
import java.lang.management.*;
import javax.management.*;
import javax.management.modelmbean.*;

public class Main{
    
    // public static void main(String[] args) throws Exception{
      // MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
      // MairieEspagne mairie = new MairieEspagne();
      // RequiredModelMBean modelMBean = new MairieEspagneModelBean();
      // modelMBean.setManagedResource(mairie, "objectReference");
      // ObjectName name = new ObjectName("model_mbean:type=OpenMXBean,name=MairieEspagnole");
      // mbs.registerMBean(modelMBean, name);
      // Thread.sleep(60*1000);
      
    // }
    
    public static void main(String[] args) throws Exception{
      ApplicationContext ctx = Factory.createApplicationContext("./model_mbean/README.TXT");

      MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

      for(ObjectName mbean : mbs.queryNames(null,null)){
          //System.out.println(mbean);
      }
      ObjectName objName = new ObjectName("model_mbean:*");
      for(ObjectInstance instance : mbs.queryMBeans(objName,null)){
          try{
              //System.out.println(instance);
              ObjectName name = instance.getObjectName();
              Object msg = mbs.invoke(name,"fonctionUniquementEspagne",null, null);
              System.out.println("msg : " + msg);
            }catch(Exception e){}
      }
      Thread.sleep(60*1000);
      
    }
}
