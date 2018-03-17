package instructions;

import commandes.CommandeI;

public abstract class Instruction<C,E,R> {

    public abstract boolean executer(C contexte, E entite, R resultat);
     
}
