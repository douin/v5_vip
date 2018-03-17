package observer;

import operations.*;

public class OperationPlus implements Operation<Nombre>{
  private int operande;
  public void setOperande(final int operande){
      this.operande = operande;
  }
  public Nombre execute(final Nombre n){
      Nombre nombre = new Nombre();
      nombre.setValeur(n.getValeur()+operande);
      return nombre;
  }
}
