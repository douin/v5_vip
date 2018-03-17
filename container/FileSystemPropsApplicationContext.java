package container;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;


/**
 * Un conteneur de beans adapté au cours NFP121, http://jfod.cnam.fr/NFP121/.
 * Injection de dépendances par mutateur.
 * Le fichier de configuration est de type "properties".<br>
 * Les beans de ce conteneur respectent les conventions d'écriture habituelles des beans<br>
 * Les propriétés de chaque bean sont :<br>
 * <pre>
 * bean.id.<i>N</i>=<i><b>nom</b>, l'identifiant unique du bean</i>
 * <i><b>nom</b></i>.class=<i>le nom de la classe</i>
 * <i><b>nom</b></i>.property.1=<i>le nom de l'attribut</i>
 * <i><b>nom</b></i>.property.1.param.1=<i>une constante, ou l'identifiant d'un bean</i>
 * <i><b>nom</b></i>.property.2=<i>le nom de l'attribut</i>
 * <i><b>nom</b></i>.property.2.param.1=<i>une constante, ou l'identifiant d'un bean</i> 
 * Les constantes sont issues des 8 types primitifs.<br>
 * Une table de constantes ou de beans comme paramètre est permis.<br>
 * 
 * </pre>
 * Avec N[1..K], N étant un nombre entier<br>
 * Ces nombres forment une suite croissante avec un incrément de 1<br>
 * Exemple : une table<br>
 * <pre>
 * bean.id.1=table
 * table.class=question1.Table
 * table.property.1=liste
 * table.property.1.param.1=listeArray
 * table.property.2=capacite
 * table.property.2.param.1=4
 * table.property.3=init
 * table.property.3.param.1=2 55 6 1
 * # 
 * bean.id.2=listeArray
 * listeArray.class=java.util.ArrayList
 * 
 * # soit en interne
 * List listeArray = new java.util.ArrayList();
 * question1.Table t = new question1.Table();
 * t.setListe(listeArray);
 * t.setCapacite(4);
 * t.setInit(new int[]{2,55,6,1});
 * 
 * assert t == ctxt.getBean("table");
 * </pre>
 * 
 * @author jm Douin
 * @version 21 Novembre 2017
 * @see java.util.Properties, Factory, AbstractApplicationContext
 */
public class FileSystemPropsApplicationContext extends AbstractApplicationContext{
  private static boolean T =  false; // T comme Trace,
  // ou verbose=true dans le fichier de configuration
  private Properties props;
  
  public FileSystemPropsApplicationContext(){
    super();
  }
 
  public void setFileName(String fileName){
    try{
      InputStream inputStream = new FileInputStream(new File(fileName));
      initialize(inputStream);
    }catch(Exception e){
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    }
  }

  public FileSystemPropsApplicationContext(InputStream inputStream){
    super();
    initialize(inputStream);
  }

  private void initialize(InputStream inputStream){
    Properties propsSystem = System.getProperties();
    String verbose = propsSystem.getProperty("verbose","false");
    try{
      T = T || Boolean.parseBoolean(verbose);
    }catch(Exception e){
    }

    this.props = new Properties();
    try{
      props.load(inputStream);     // chargement des propriétés
      try{
        T = T || Boolean.parseBoolean(props.getProperty("verbose","false"));
      }catch(Exception e){
      }
      //if(T)System.out.println(props.toString());
      verifyProperties(); // vérification du contenu, suite croissante, etc...
      analyzeProperties();// injections par mutateurs
    }catch(Exception e){
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    }
  }

  /** Quelques vérifications du fichier de properties, loin d'être exaustives
   * 
   */
  private void verifyProperties() throws RuntimeException{
    List<Object> cles = new ArrayList<Object>(props.keySet());
    Properties properties = new Properties(props); // copie par prévention
    String premier = properties.getProperty("bean.id.1");
    if(premier==null) throw new RuntimeException("bean.id.1 est absent ???");
    int indexBean=1;
    int somme = 0;
    String id = properties.getProperty("bean.id." +indexBean); // de 1 à N
    while(id!=null){

      String className = properties.getProperty(id+".class");
      if(className==null) throw new RuntimeException("id présent, mais pas "+id+".class");

      int indexProperty = 1; 
      String propertyName = properties.getProperty(id+".property."+indexProperty);
      while(propertyName!=null){        
        String propertyId = properties.getProperty(id+".property."+indexProperty+".param.1");
        if(propertyId==null)throw new RuntimeException("property présent, mais pas "+id+".property."+indexProperty+".param.1");
        indexProperty++;
        propertyName = properties.getProperty(id+".property."+indexProperty); 
      }
      somme = somme + indexBean;
      cles.remove("bean.id." +indexBean);
      indexBean++;
      id = properties.getProperty("bean.id." +indexBean);
    }

    indexBean--;
    // une suite croissante
    int s = (indexBean*(indexBean+1))/2;// somme des n premiers nombres
    if(s!=somme)throw new RuntimeException("Les *.id.N, ne forment pas une suite croissante ...");
    // cas des clefs restantes
    for(Object c : cles){
      if(((String)c).contains("bean.id")) //il reste (au moins) une clef
        throw new RuntimeException("les id, ne forment pas une suite croissante ...");
    } 
  }

  private void analyzeProperties() throws RuntimeException{
    List<String> beanIdList =new ArrayList<String>();
    try{
      int indexBean=1;
      String id = props.getProperty("bean.id." +indexBean);
      while(id!=null){ 
        beanIdList.add("bean.id." +indexBean);
        if(beans.keySet().contains(id)) throw new RuntimeException("id déjà présent, "+id);

        String className = props.getProperty(id+".class");
        if(className==null)throw new RuntimeException("id présent, mais pas de "+id+".class");
        if(T)System.out.println("className: " + className);
        Class<?> beanClass = null;
        try{
          beanClass = Class.forName(className);
        }catch(ClassNotFoundException e){
          try{
            beanClass = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
          }catch(ClassNotFoundException exc){
            throw new RuntimeException(className + " ClassNotFoundException");
          }
        }
        beans.put(id,beanClass.newInstance()); // creation de tous les beans
        if(T)System.out.println("id: " + id + ", creation de : " + beanClass.getSimpleName());
        indexBean++;
        id = props.getProperty("bean.id." +indexBean);
      }

      // Intialisation dans l'ordre alphabétique des noms des beans...
      // for(String idBean : beans.keySet()){
      //   if(T)System.out.println(idBean +  ", appels des mutateurs:");
        // initializePropertiesBean(idBean);
      // }
      
      //Initialisation dans l'ordre des numéros des beans
      for(String idNumber : beanIdList){
          String idBean = props.getProperty(idNumber);
          if(T)System.out.println("id: " +idNumber + ", " + idBean +  ", appels des mutateurs:");
          initializePropertiesBean(idBean);
      }


    }catch(Exception e){
      throw new RuntimeException(e.getMessage());
    }
  }

  
  private void initializePropertiesBean(String id){
    int indexProperty = 1;
    boolean hasNextProperty = true;
    Object bean = beans.get(id);
    String propertyName = props.getProperty(id+".property."+indexProperty);
    while(bean!=null && propertyName!=null){
      try{
        // un setter ne peut avoir qu'un seul paramètre, soit .param.1
        String propertyId = props.getProperty(id+".property."+indexProperty+".param.1");
        try{
          // conversion habituelle, 1ère lettre de l'attribut en Majuscule
          // private type property;
          String prop = Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
          // setProperty(???) est recherchée
          Method setter  = findMethod(id, "set"+prop, propertyName);
          // le type attendu (???) est :
          Class<?> classExpected = setter.getParameterTypes()[0];
          //Method setter  = findMethod(id, "set"+prop);
          Object arg = beans.get(propertyId); // arg != null, c'est un bean existant
          if(classExpected.isArray() || arg==null){ // arg==null, ce ne peut être qu'une constante ou un tableau
            if(T)System.out.println("\t"+id+"\tset"+prop  + "(" + propertyId + ")");
            arg = newInstance(classExpected, propertyId);
          }else{
            if(T)System.out.println("\t"+id+"\t\tset"+prop  + "(" + arg + ")");
          }
          setter.invoke(bean, arg);
        }catch(Exception e){
          //if(T)e.printStackTrace();
          //throw new RuntimeException(e.getMessage());
          //if(T)System.out.println("Exception " + e.getMessage() + ", id= " + id + ".property."+indexProperty+ "." + propertyName);
        }
        indexProperty++;
        propertyName = props.getProperty(id+".property."+indexProperty);
      }catch(Exception e){
        if(T)e.printStackTrace();
        throw new RuntimeException(e.getMessage());
      }
    }
  }

  //   // à terminer ...
  //    private void initializeOperationsBean(String id){
  //     int indexOperation = 1;
  //     boolean hasNextOperation = true;
  //     Object bean = beans.get(id);
  //     String operationName = props.getProperty(id+".operation."+indexOperation);
  //     while(bean!=null && operationName!=null){
  //       try{
  //         // une opération peut avoir plusieurs paramètres,
  //         String operationId = props.getProperty(id+".operation."+indexOperation+".param."+indexParam);
  // 
  //       }catch(Exception e){
  //         if(T)e.printStackTrace();
  //         throw new RuntimeException(e.getMessage());
  //       }
  //     }
  //   }


  private Method findMethod(String id, String methodName, String propertyName){
    Class<?> propertyClass = null;
    Class<?> cl = beans.get(id).getClass();
    while(cl!=Object.class && propertyClass==null){
      try{
        // cet attribut est-il déclaré dans cette classe ?
        Field f = cl.getDeclaredField(propertyName);
        propertyClass = f.getType(); 
      }catch(Exception e){
      }finally{
        // remontée de l'arbre d'héritage à la recherche de cet attribut
        cl = cl.getSuperclass(); 
      }
    }
    // si propertyClass == null, 
    //   il n'y a pas d'attribut avec ce nom mais le setter est en place, 
    //   une délégation par exemple...
    if(T && propertyClass==null)
      System.out.println("\t"+id+"\t"+propertyName + " n'existe pas ???");
    try{
      return beans.get(id).getClass().getMethod(methodName,propertyClass); 
    }catch(Exception e){
      // ici, un setter mais pas d'attribut i.e. propertyClass==null
      // attention si plusieurs méthodes avec le même nom, peu probable pour un setter
      for(Method m : beans.get(id).getClass().getMethods()){
        if(m.getName().equals(methodName)){    // même nom
          if((m.getParameterTypes().length==1))// un setter a une arité de 1
            return m;
        }
      }
    }
    return null;
  }

  // nouvelle instance de l'objet à injecter, objet issu d'une constante
  // une constante en 8 types primitifs possibles et leur wrapper
  // Les tableaux dont les éléments sont des beans ou "wrapper" sont permis 
  // seuls les types int[].class et float[].class ont été implémentés
  /** Obtention d'une nouvelle instance à injecter.
   * @param m le "setter", set<i>Attribut</i>
   * @param str la valeur extraite du fichier de configuration
   */
  private Object newInstance(Class<?> cl, String str){
    try{
      // cas particulier
      if(str.equals("null")) return null;          // constante null
      if(str.endsWith(".class"))return Class.forName(str.substring(0, str.length()-6));
      if(cl.isArray()) return parseArray(cl, str);
      // c'est une constante, appel de Type.parseType(String.class)
      return map.get(cl).invoke(null,str);
    }catch(Exception e){
      throw new RuntimeException(e.getMessage());
    }
  }

  // private Object[] newInstance(Method m, String[] str){
  // Object[] res = new Object[m.getParameterCount()];
  // try{
  // for(int i=0;i<res.length;i++){
  // res[i] = newInstance(m.getParameterTypes()[i], str[i]);
  // }
  // return res;
  // }catch(Exception e){
  // throw new RuntimeException(e.getMessage());
  // }
  // }

  private Object parseArray(Class<?> cl, String str) throws Exception{
    Class<?> elementClass = cl.getComponentType();
    String[] t = str.split(" ");
    Object tab = Array.newInstance(elementClass, t.length);
    for(int i=0;i<t.length;i++){
      Object elt = beans.get(t[i]);
      if(elt==null) { // ce n'est pas un bean du conteneur
        // appel du parse associé au type de cette constante
        //elt = map.get(elementClass).invoke(null,t[i]);
        elt = newInstance(elementClass, t[i]);
      }
      Array.set(tab, i, elt);
    }
    return tab;
  }

  //   private Object parsePrimitiveArray(Class<?> cl, String str) throws Exception{
  //     Class<?> elementClass = cl.getComponentType();
  //     String[] t = str.split(" ");
  //     if(elementClass==float.class){
  //       float[] tf = new float[t.length];
  //       for(int i=0;i<tf.length;i++)
  //         tf[i] = Float.parseFloat(t[i]);
  //       return tf;
  //     }else if(elementClass==int.class){
  //       int[] ti = new int[t.length];
  //       for(int i=0;i<ti.length;i++)
  //         ti[i] = Integer.parseInt(t[i]);
  //       return ti;
  //     }
  //     throw new RuntimeException(" primitives array, partiellement implementés...");
  //   }

  private static char parseChar(String str){
    return str.charAt(0);
  } 

  private static String parseString(String str){
    return new String(str);
  }
  private static Map<Class<?>, Method> map;
  static{
    try{
      map = new HashMap<Class<?>, Method>();
      map.put(byte.class, Byte.class.getMethod("parseByte",String.class));
      map.put(Byte.class, Byte.class.getMethod("parseByte",String.class));
      map.put(short.class, Short.class.getMethod("parseShort",String.class));
      map.put(Short.class, Short.class.getMethod("parseShort",String.class));
      map.put(int.class, Integer.class.getMethod("parseInt",String.class));
      map.put(Integer.class, Integer.class.getMethod("parseInt",String.class));
      map.put(long.class, Long.class.getMethod("parseLong",String.class));
      map.put(Long.class, Long.class.getMethod("parseLong",String.class));
      map.put(float.class, Float.class.getMethod("parseFloat",String.class));
      map.put(Float.class, Float.class.getMethod("parseFloat",String.class));
      map.put(double.class, Double.class.getMethod("parseDouble",String.class));
      map.put(Double.class, Double.class.getMethod("parseDouble",String.class));
      map.put(boolean.class, Boolean.class.getMethod("parseBoolean",String.class));
      map.put(Boolean.class, Boolean.class.getMethod("parseBoolean",String.class));      
      map.put(char.class, FileSystemPropsApplicationContext.class.
        getDeclaredMethod("parseChar",String.class));
      map.put(Character.class, FileSystemPropsApplicationContext.class.
        getDeclaredMethod("parseChar",String.class));
      map.put(String.class, FileSystemPropsApplicationContext.class.
        getDeclaredMethod("parseString",String.class));      

    }catch(Exception e){
    }
  }

}
//   en moins concis mais plus rapide en temps d'exécution...
//     if(cl==byte.class || cl==Byte.class) return Byte.parseByte(str);
//     if(cl==short.class || cl==Short.class) return Short.parseShort(str);
//     if(cl==int.class || cl==Integer.class) return Integer.parseInt(str);
//     if(cl==long.class || cl==Long.class) return Long.parseLong(str);
//     if(cl==double.class || cl==Double.class) return Double.parseDouble(str);
//     if(cl==float.class || cl==Float.class) return Float.parseFloat(str);
//     if(cl==boolean.class || cl==Boolean.class) return Boolean.parseBoolean(str);
//     if(cl==void.class || cl==Void.class)return null;

//       try{
//         // attention au cas parseInt ... 
//         Method parse = cl.getMethod("parse"+cl.getSimpleName(), String.class);
//         return parse.invoke(this,str);
//       }catch(Exception e){
//         throw new RuntimeException("method not found : " + cl.getSimpleName());
//       }

//     throw new RuntimeException("class not found : " + cl.getSimpleName());
