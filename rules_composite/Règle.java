package rules_composite;

public abstract class R�gle<E,R>{
  public abstract R�gle si(Condition<E> condition);
  public abstract R�gle alors(Op�ration<E,R> operation);
}
