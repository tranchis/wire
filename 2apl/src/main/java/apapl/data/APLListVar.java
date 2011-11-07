package apapl.data;

/**
 * A variable ranging over lists.
 */
public abstract class APLListVar extends Term
{
	/**
	 * Clones this list variable.
	 * 
	 * @return the clone
	 */
	public abstract APLListVar clone();
}