package model_mbean;


import java.util.*;

import container.*;
import java.lang.management.*;
import javax.management.*;
import javax.management.modelmbean.*;

public class ProxyModelMBeanTests extends junit.framework.TestCase{

    public void testMairieMBean() throws Exception{
      ApplicationContext ctx = Factory.createApplicationContext("./model_mbean/README.TXT");
    
    }
      
    public void testMBeanSansInjection() throws Exception{
      MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
      MairieEspagne mairie = new MairieEspagne();
      RequiredModelMBean modelMBean = new MairieEspagneModelMBean();
      modelMBean.setManagedResource(mairie, "objectReference");
      ObjectName name = new ObjectName("model_mbean:type=OpenMXBean,name=MairieEspagnole");
      mbs.registerMBean(modelMBean, name);
      Thread.sleep(60*1000);
      
    }
  
}

