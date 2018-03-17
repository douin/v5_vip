package conditions;



import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import conditions.ConditionI;

public class OperateursTests{

    /**
     * Constructeur de la classe-test OperateursTests
     */
    public OperateursTests()
    {
    }

    /**
     * Met en place les engagements.
     *
     * Méthode appelée avant chaque appel de méthode de test.
     */
    @Before
    public void setUp() // throws java.lang.Exception
    {
    }

    /**
     * Supprime les engagements
     *
     * Méthode appelée après chaque appel de méthode de test.
     */
    @After
    public void tearDown() // throws java.lang.Exception
    {
        //Libérez ici les ressources engagées par setUp()
    }
    
    private static class Impair implements ConditionI<Integer>{
       public boolean estSatisfaite(Integer i){
           return (i%2)==1;
       }
    }
    private static class Pair implements ConditionI<Integer>{
       public boolean estSatisfaite(Integer i){
           return (i%2)==0;
       }
    }    
    
    @Test
    public void EtOuTest(){
        assertTrue(new VRAI().estSatisfaite(null));
        assertFalse(new FAUX().estSatisfaite(null));
        ConditionI<Integer> cond1 = new VRAI();
        ConditionI<Integer> cond2 = new FAUX();
        
        Et<Integer> et = new Et();
        et.setCond1(cond1);et.setCond2(cond2);
        assertFalse(et.estSatisfaite(null));
        Ou<Integer> ou = new Ou();
        ou.setCond1(et);ou.setCond2(et);
        assertFalse(ou.estSatisfaite(null));
        ou.setCond2(new VRAI());
        assertTrue(ou.estSatisfaite(null));
        
        cond1 = new Pair();cond2 = new Impair();
        et.setCond1(cond1);et.setCond2(cond2);
        assertFalse(et.estSatisfaite(3));
        ou.setCond1(cond1);ou.setCond2(cond2);
        assertTrue(ou.estSatisfaite(3));
    }
    
    @Test
    public void EtOuMacroConditionTest(){
      MacroCondition<Integer> cond1 = new UneDesConditionsEstSatisfaite();
      ConditionI<Integer>[] conditions = new ConditionI[]{new VRAI(), new Pair(), new Impair()};
      cond1.setConditions(conditions);
      assertTrue(cond1.estSatisfaite(3));
      MacroCondition<Integer> cond2 = new ToutesLesConditionsSontSatisfaites();
      cond2.setConditions(conditions);
      assertFalse(cond2.estSatisfaite(3));
      Ou<Integer> ou = new Ou();
      ou.setCond1(cond1);ou.setCond2(cond2);
      assertTrue(ou.estSatisfaite(2));
      Et<Integer> et = new Et();
      et.setCond1(cond1);et.setCond2(cond2);
      assertFalse(et.estSatisfaite(2));
    }
}
