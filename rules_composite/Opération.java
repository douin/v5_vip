package rules_composite;


public interface Opération<E,R>{
 
    public void exécuter(E entité, R résultat);
}
