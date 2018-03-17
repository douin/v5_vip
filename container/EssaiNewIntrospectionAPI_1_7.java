package container;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import java.lang.reflect.*;


// https://www.javaworld.com/article/2860079/learn-java/invokedynamic-101.html

public class EssaiNewIntrospectionAPI_1_7{
    
    public static class Agent{
        private String nom;
        public String prenom;
        public String toString(){
            return "<" + nom +", " + prenom +">";
        }
    }
    
    public static void main(String[] args)throws Throwable{

        Class cl = Class.forName("container.EssaiNewIntrospectionAPI_1_7$Agent");
        Object obj = cl.newInstance();
        
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        // prenom est public...
        MethodHandle mh = lookup.findSetter(cl, "prenom", String.class);
        mh.invoke(obj,"Paul");
        System.out.println("agent: " + obj);
        
        // nom est private...
        // mh = lookup.findSetter(cl, "nom", String.class); --> exception
        Field field = cl.getDeclaredField("nom");
        field.setAccessible(true);
        mh = lookup.unreflectGetter(field);
        //MethodHandle mh = lookup.findSetter(cl, "nom", String.class);
        //mh.invoke(obj,"Personne"); --> exception
        field.set(obj, "Personne");
        System.out.println("agent: " + obj);
    }
}
