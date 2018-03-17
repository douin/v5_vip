package contexte;
import java.util.*;

public interface AgentF extends AgentI{
  // public int getAge();
  
  // public int getAnciennete();
  // public int getNombreTotalDeJoursDeConges();
  // public int getNombreDeJoursDeCongesUtilises();
  
  public MairieF getContexte();
  public boolean getEstTitulaire();
  public boolean getEstUltraMarin();
  
  public class Couple{
      public Couple(int nombreDeSemaines, float nombreDeJoursTravailles){
          this.nombreDeSemaines = nombreDeSemaines;
          this.nombreDeJoursTravailles = nombreDeJoursTravailles;
        }
      public int nombreDeSemaines;
      public float nombreDeJoursTravailles;
    }
  /** @return une table de couples du nombre de semaines et du nombre de jours travaillé
   */
  public List<Couple> getObligationHebdomadaireDeService();
  public int getJoursDeCongesPrisEntreMaiEtOctobre();
  public boolean getEstAnnualise();
  
}