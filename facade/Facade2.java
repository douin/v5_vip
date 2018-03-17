package facade;

public class Facade2 implements FacadeI {

  ClasseA classeA;
  ClasseB classeB;
  ClasseC classeC;
  ClasseD classeD;

  public Facade2() {
    classeA = new ClasseA();
    classeB = new ClasseB();
    classeC = new ClasseC();
    classeD = new ClasseD();
  }

  public void methode2() {
    System.out.println("Methode2 : ");
    classeA.methodeA();
    classeD.methodeD();
    classeC.methodeC();
  }

  public void methode1() {
    System.out.println("Methode1 : ");
    classeB.methodeB();
    classeC.methodeC();
  }
  
      
}