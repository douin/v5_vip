package conditions;


public class UneEtUneSeuleDesConditionsEstSatisfaite<E> extends MacroCondition<E>{
     public boolean estSatisfaite(E e){
        int compteur = 0;
        for(ConditionI<E> condition : conditions){
            if(condition.estSatisfaite(e))compteur++;
        }
        return compteur==1;
    }
}