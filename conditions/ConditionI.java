package conditions;

/** The VIP framework:  la condition<br>
 * si <b>condition(entit�)</b> alors ...<br>
 * @param <E> l'entit�
 */
public interface ConditionI<E> extends java.io.Serializable{

    public boolean estSatisfaite(E e);
         
}