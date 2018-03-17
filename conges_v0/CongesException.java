package conges_v0;

public class CongesException implements Operation<AgentI,ResultatConges>{
  private String message;
  public void setMessage(final String message){
      this.message = message;
  }
  
  public void executer(final AgentI agent, ResultatConges resultat){
     System.out.println("Exception: " + message);
     System.out.println("\t\t" + agent + "\t" + resultat);
  }
}

