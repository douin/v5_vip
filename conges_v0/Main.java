package conges_v0;
import container.*;

public class Main{
    private static class Contexte implements ContexteI{
        public String getLieu(){ return "Sainte-Engrâce";}

        public int getNombreHabitants(){ return 77;}

        public boolean enMetropole(){return true;}
    }

    public static void main(String[] args){
        ApplicationContext ctx = Factory.createApplicationContext("./conges/README.TXT");

        AgentI agent = new AgentI(){
                public ContexteI getContexte(){return new Contexte();}
                public int getAnciennete(){return 10;}
                public int getNombreTotalDeJoursDeConges(){return 35;}
                public int getNombreDeJoursDeCongesUtilises(){return 15;}
                public boolean getEstUltraMarin(){return true;}
            };
        ResultatConges resultat = new ResultatConges();
        try{
            Invoker<AgentI,ResultatConges> invoker = (Invoker)ctx.getBean("invoker");
            invoker.execute(agent, resultat);
        }catch(Exception e){ 
            System.out.println("exception: " + e.getMessage());
        }finally{
           System.out.println("resultat: " + resultat.getJoursDeConges());
        }
        
    }
}
    
