package interceptor;


public class MairieE implements MairieI{
    private int nombreDHabitants;
    
    public void setNombreDHabitants(int nombre){
        this.nombreDHabitants = nombre;
    }
    public int nombreDHabitants(){
        return this.nombreDHabitants;
    }
    
    public int specifique(){
        return 100;
    }
  
}
