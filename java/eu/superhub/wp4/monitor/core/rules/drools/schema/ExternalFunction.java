//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.10.07 at 04:10:50 PM CEST 
//

package eu.superhub.wp4.monitor.core.rules.drools.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="evaluator" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="expression" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "external-function")
public class ExternalFunction {

	@XmlAttribute(required = true)
	protected String evaluator;
	@XmlAttribute(required = true)
	protected String expression;

	/**
	 * Gets the value of the evaluator property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getEvaluator() {
		return evaluator;
	}

	/**
	 * Sets the value of the evaluator property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setEvaluator(String value) {
		this.evaluator = value;
	}

	/**
	 * Gets the value of the expression property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * Sets the value of the expression property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setExpression(String value) {
		this.expression = value;
	}

}
