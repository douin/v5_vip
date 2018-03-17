package facade;

public class Facade1 implements FacadeI {

  ClasseA classeA;
  ClasseB classeB;
  ClasseC classeC;
  ClasseD classeD;

  public Facade1() {
    classeA = new ClasseA();
    classeB = new ClasseB();
    classeC = new ClasseC();
    classeD = new ClasseD();
  }

  public void methode1() {
    System.out.println("Methode1 : ");
    classeA.methodeA();
    classeD.methodeD();
    classeC.methodeC();
  }

  public void methode2() {
    System.out.println("Methode2 : ");
    classeB.methodeB();
    classeC.methodeC();
  }
  
      
}