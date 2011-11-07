package apapl.data;

/**
 * A tuple consisting of two elements. Introduced to facilitate the return value of some
 * methods.
 * 
 * @param <T1> type of left object
 * @param <T2> type of right object
 */
public class Tuple<T1,T2>
{
    public T1 l;
	public T2 r;

	/**
	 * Constructs a tuple.
	 * 
	 * @param l left
	 * @param r right
	 */
	public Tuple(T1 l, T2 r)
	{
		this.l = l;
		this.r = r;
	}
}
