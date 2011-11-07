package apapl;

/**
 * Defines an object that can be subject to a substitution.
 * This distinguishing property is used by the {@link apapl.SubstList}. 
 * In general, terms and plans can be subject to a substitution.
 */
public interface Substitutable
{
	/**
	 * Convert the object to a representation in rich text format.
	 * 
	 * @return the RTF representation of this object
	 */
	public String toRTF();
}
	
