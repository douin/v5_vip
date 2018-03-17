package conditions;

/** The VIP framework:  la condition<br>
 * si <b>condition(entité)</b> alors ...<br>
 * @param <E> l'entité
 */
public interface ConditionI<E> extends java.io.Serializable{

    public boolean estSatisfaite(E e);
         
}