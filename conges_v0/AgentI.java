package conges_v0;

public interface AgentI{
  public ContexteI getContexte();
  public int getAnciennete();
  public int getNombreTotalDeJoursDeConges();
  public int getNombreDeJoursDeCongesUtilises();
  public boolean getEstUltraMarin();
}
