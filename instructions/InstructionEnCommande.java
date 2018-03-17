package instructions;

import commandes.CommandeI;
public class InstructionEnCommande<C,E,R> implements CommandeI<E,R> {
  protected Instruction<C,E,R> instruction;
  public void setInstruction(Instruction<C,E,R> instruction){
      this.instruction = instruction;
  }
  protected C contexte;
  
  public void setContexte(C contexte){
      this.contexte =contexte;
    }
  public boolean executer(E entite, R resultat){
     return instruction.executer(contexte,entite,resultat);
  }
  
}
