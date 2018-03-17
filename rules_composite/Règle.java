package rules_composite;

public abstract class Règle<E,R>{
  public abstract Règle si(Condition<E> condition);
  public abstract Règle alors(Opération<E,R> operation);
}
