package model_mbean;
import java.lang.management.*;
import javax.management.*;
import javax.management.modelmbean.*;

public class MairieEspagneModelMBean extends RequiredModelMBean{
 
  
    public MairieEspagneModelMBean()throws Exception{
        super(creerMBeanInfo());
    }
    
    public void setManagedResource(Object object) throws Exception{
        this.setManagedResource(object, "objectReference");
    }
    
    private ObjectName objectName;
    
    public void setName(String name)throws Exception{
      this.objectName = new ObjectName(name);
      ManagementFactory.getPlatformMBeanServer().registerMBean(this, objectName);
    }
    
    public ObjectName getObjectName(){
        return this.objectName;
    }
    
    private static ModelMBeanInfo creerMBeanInfo(){
        ModelMBeanConstructorInfo[] mConsInfo = new ModelMBeanConstructorInfo[1];
        mConsInfo[0] = new ModelMBeanConstructorInfo("MairieEspagne", "le constructeur", null);
       
        ModelMBeanOperationInfo[] mOperInfo = new ModelMBeanOperationInfo[1];
        mOperInfo[0] = new ModelMBeanOperationInfo("fonctionUniquementEspagne", 
                                                   "accès à la fonction unique",
                                                    null,
                                                   "String",
                                                   ModelMBeanOperationInfo.INFO);
        
        return new ModelMBeanInfoSupport("model_mbean.MairieEspagne",
                                         "BL exemple ?",
                                         null, // pas d'attribut
                                         mConsInfo,
                                         mOperInfo,
                                         null);
        
    }
    
    
    
      
}
