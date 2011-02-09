package net.sf.ictalive.monitoring.rules.drools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.sf.ictalive.monitoring.rules.drools.schema.Package;

public class XSDRuleParser
{
	private Marshaller				m;
	private Unmarshaller			u;
	
	public XSDRuleParser() throws JAXBException
	{
		JAXBContext	jc;
		
		jc = JAXBContext.newInstance(Package.class);
		m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		u = jc.createUnmarshaller();
	}

	public String serialise(Package p) throws JAXBException
	{
		ByteArrayOutputStream	baos;
		String					res;
		
		baos = new ByteArrayOutputStream();
		m.marshal(p, baos);
		res = baos.toString();
		
		return res;
	}

	private Object deserialise(String xml) throws JAXBException
	{
		ByteArrayInputStream	bais;
		Object					res;
		
		bais = new ByteArrayInputStream(xml.getBytes());
		res = u.unmarshal(bais);
		
		return res;
	}
	
	public static void main(String args[]) throws JAXBException
	{
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
				"<package xmlns=\"http://drools.org/drools-5.0\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\" name=\"net.sf.ictalive.monitoring.domain\" xs:schemaLocation=\"http://drools.org/drools-5.0 drools.org/drools-5.0.xsd\">\n" + 
				"	<import name=\"java.util.TreeSet\"/>\n" + 
				"	<import name=\"java.util.Iterator\"/>\n" + 
				"	<import name=\"net.sf.ictalive.opera.OM.Norm\"/>\n" + 
				"	<import name=\"net.sf.ictalive.EventModel.Event.Event\"/>\n" + 
				"	<rule name=\"event received\">\n" + 
				"		<lhs>\n" + 
				"			<pattern identifier=\"ev\" object-type=\"Event\">\n" + 
				"\n" + 
				"\n" + 
				"</pattern>\n" + 
				"		</lhs>\n" + 
				"		<rhs>		System.out.println(\"Event received from asserter: \" + ev.getAsserter().getName());\n" + 
				"</rhs>\n" + 
				"	</rule>\n" + 
				"	<rule name=\"there is a set called Roles\">\n" + 
				"		<lhs>\n" + 
				"			<not>\n" + 
				"				<pattern object-type=\"Roles\">\n" + 
				"\n" + 
				"\n" + 
				"</pattern>\n" + 
				"			</not>\n" + 
				"		</lhs>\n" + 
				"		<rhs>		insert(new Roles());\n" + 
				"</rhs>\n" + 
				"	</rule>\n" + 
				"	<rule name=\"the set of all roles is Roles\">\n" + 
				"		<lhs>\n" + 
				"			<and-conditional-element>\n" + 
				"				<pattern identifier=\"r\" object-type=\"Role\">\n" + 
				"\n" + 
				"\n" + 
				"</pattern>\n" + 
				"				<pattern identifier=\"sr\" object-type=\"Roles\">\n" + 
				"\n" + 
				"\n" + 
				"</pattern>\n" + 
				"			</and-conditional-element>\n" + 
				"		</lhs>\n" + 
				"		<rhs>		sr.add(r);\n" + 
				"</rhs>\n" + 
				"	</rule>\n" + 
				"	<rule name=\"creation of running contexts\">\n" + 
				"		<lhs>\n" + 
				"			<and-conditional-element>\n" + 
				"				<pattern object-type=\"ClassificatoryCountsAs\">\n" + 
				"					<field-binding field-name=\"c1\" identifier=\"a\"/>\n" + 
				"					<field-binding field-name=\"c2\" identifier=\"b\"/>\n" + 
				"				</pattern>\n" + 
				"				<pattern identifier=\"lc\" object-type=\"TreeSet\">\n" + 
				"					<from>\n" + 
				"						<collect>\n" + 
				"							<pattern object-type=\"ClassificatoryCountsAs\">\n" + 
				"								<field-constraint field-name=\"c1\">\n" + 
				"									<variable-restriction evaluator=\"==\" identifier=\"a\"/>\n" + 
				"								</field-constraint>\n" + 
				"								<field-constraint field-name=\"c2\">\n" + 
				"									<variable-restriction evaluator=\"==\" identifier=\"b\"/>\n" + 
				"								</field-constraint>\n" + 
				"							</pattern>\n" + 
				"						</collect>\n" + 
				"					</from>\n" + 
				"				</pattern>\n" + 
				"			</and-conditional-element>\n" + 
				"		</lhs>\n" + 
				"		<rhs>		RunningContext	rc;\n" + 
				"		\n" + 
				"		rc = new RunningContext(lc);\n" + 
				"		insertLogical(rc);\n" + 
				"</rhs>\n" + 
				"	</rule>\n" + 
				"	<rule name=\"identify running contexts\">\n" + 
				"		<lhs>\n" + 
				"			<and-conditional-element>\n" + 
				"				<pattern identifier=\"cca\" object-type=\"ClassificatoryCountsAs\">\n" + 
				"					<field-binding field-name=\"context\" identifier=\"c\"/>\n" + 
				"				</pattern>\n" + 
				"				<pattern identifier=\"rc\" object-type=\"RunningContext\">\n" + 
				"					<field-constraint field-name=\"countsas\">\n" + 
				"						<variable-restriction evaluator=\"contains\" identifier=\"cca\"/>\n" + 
				"					</field-constraint>\n" + 
				"				</pattern>\n" + 
				"			</and-conditional-element>\n" + 
				"		</lhs>\n" + 
				"		<rhs>		insertLogical(new RunningContextIdentifier(rc, c));\n" + 
				"</rhs>\n" + 
				"	</rule>\n" + 
				"	<rule name=\"cleaning running contexts\">\n" + 
				"		<lhs>\n" + 
				"			<and-conditional-element>\n" + 
				"				<pattern identifier=\"rc\" object-type=\"RunningContext\">\n" + 
				"					<field-binding field-name=\"countsas\" identifier=\"ca\"/>\n" + 
				"				</pattern>\n" + 
				"				<pattern identifier=\"rc2\" object-type=\"RunningContext\">\n" + 
				"					<field-constraint field-name=\"countsas\">\n" + 
				"						<variable-restriction evaluator=\"==\" identifier=\"ca\"/>\n" + 
				"					</field-constraint>\n" + 
				"				</pattern>\n" + 
				"			</and-conditional-element>\n" + 
				"		</lhs>\n" + 
				"		<rhs>		if(rc != rc2)\n" + 
				"		{\n" + 
				"			retract(rc2);\n" + 
				"		}\n" + 
				"</rhs>\n" + 
				"	</rule>\n" + 
				"	<rule name=\"running contexts\">\n" + 
				"		<lhs>\n" + 
				"			<pattern identifier=\"rc\" object-type=\"TreeSet\">\n" + 
				"				<from>\n" + 
				"					<collect>\n" + 
				"						<pattern object-type=\"RunningContext\">\n" + 
				"\n" + 
				"\n" + 
				"</pattern>\n" + 
				"					</collect>\n" + 
				"				</from>\n" + 
				"			</pattern>\n" + 
				"		</lhs>\n" + 
				"		<rhs>		System.out.println(rc);\n" + 
				"</rhs>\n" + 
				"	</rule>\n" + 
				"	<rule name=\"intersection\">\n" + 
				"		<lhs>\n" + 
				"			<and-conditional-element>\n" + 
				"				<and-conditional-element>\n" + 
				"					<and-conditional-element>\n" + 
				"						<pattern identifier=\"obj\" object-type=\"Object\">\n" + 
				"\n" + 
				"\n" + 
				"</pattern>\n" + 
				"						<pattern object-type=\"Predicate\">\n" + 
				"							<field-binding field-name=\"class\" identifier=\"y1\"/>\n" + 
				"							<field-constraint field-name=\"object\">\n" + 
				"								<variable-restriction evaluator=\"==\" identifier=\"obj\"/>\n" + 
				"							</field-constraint>\n" + 
				"						</pattern>\n" + 
				"					</and-conditional-element>\n" + 
				"					<pattern object-type=\"Predicate\">\n" + 
				"						<field-binding field-name=\"class\" identifier=\"y2\"/>\n" + 
				"						<field-constraint field-name=\"object\">\n" + 
				"							<variable-restriction evaluator=\"==\" identifier=\"obj\"/>\n" + 
				"						</field-constraint>\n" + 
				"					</pattern>\n" + 
				"				</and-conditional-element>\n" + 
				"				<pattern object-type=\"Intersection\">\n" + 
				"					<field-constraint field-name=\"c1\">\n" + 
				"						<variable-restriction evaluator=\"==\" identifier=\"y1\"/>\n" + 
				"					</field-constraint>\n" + 
				"					<field-constraint field-name=\"c2\">\n" + 
				"						<variable-restriction evaluator=\"==\" identifier=\"y2\"/>\n" + 
				"					</field-constraint>\n" + 
				"					<field-binding field-name=\"intersection\" identifier=\"i\"/>\n" + 
				"				</pattern>\n" + 
				"			</and-conditional-element>\n" + 
				"		</lhs>\n" + 
				"		<rhs>		Predicate instance;\n" + 
				"		instance = (Predicate)(((Class)i).newInstance());\n" + 
				"		instance.setObject(obj);\n" + 
				"		insertLogical(instance);\n" + 
				"</rhs>\n" + 
				"	</rule>\n" + 
				"	<rule name=\"counts-as\">\n" + 
				"		<lhs>\n" + 
				"			<and-conditional-element>\n" + 
				"				<pattern identifier=\"c\" object-type=\"CountsAs\">\n" + 
				"					<field-binding field-name=\"c1\" identifier=\"y1\"/>\n" + 
				"					<field-binding field-name=\"c2\" identifier=\"y2\"/>\n" + 
				"				</pattern>\n" + 
				"				<pattern identifier=\"obj\" object-type=\"Predicate\">\n" + 
				"					<field-constraint field-name=\"class\">\n" + 
				"						<variable-restriction evaluator=\"==\" identifier=\"y1\"/>\n" + 
				"					</field-constraint>\n" + 
				"				</pattern>\n" + 
				"			</and-conditional-element>\n" + 
				"		</lhs>\n" + 
				"		<rhs>		Predicate instance;\n" + 
				"		instance = (Predicate)(((Class)y2).newInstance());\n" + 
				"		instance.setObject(obj.getObject());\n" + 
				"		insertLogical(instance);\n" + 
				"</rhs>\n" + 
				"	</rule>\n" + 
				"	<rule name=\"activate running contexts\">\n" + 
				"		<lhs>\n" + 
				"			<and-conditional-element>\n" + 
				"				<pattern object-type=\"ContextActive\">\n" + 
				"					<field-binding field-name=\"context\" identifier=\"c\"/>\n" + 
				"				</pattern>\n" + 
				"				<pattern object-type=\"RunningContextIdentifier\">\n" + 
				"					<field-binding field-name=\"runningContext\" identifier=\"rc\"/>\n" + 
				"					<field-constraint field-name=\"context\">\n" + 
				"						<variable-restriction evaluator=\"==\" identifier=\"c\"/>\n" + 
				"					</field-constraint>\n" + 
				"				</pattern>\n" + 
				"			</and-conditional-element>\n" + 
				"		</lhs>\n" + 
				"		<rhs>		insertLogical(new RunningContextActive(rc));\n" + 
				"</rhs>\n" + 
				"	</rule>\n" + 
				"	<rule name=\"classificatory counts-as\">\n" + 
				"		<lhs>\n" + 
				"			<and-conditional-element>\n" + 
				"				<and-conditional-element>\n" + 
				"					<pattern identifier=\"ca\" object-type=\"ClassificatoryCountsAs\">\n" + 
				"						<field-binding field-name=\"c1\" identifier=\"y1\"/>\n" + 
				"						<field-binding field-name=\"c2\" identifier=\"y2\"/>\n" + 
				"					</pattern>\n" + 
				"					<pattern identifier=\"rc\" object-type=\"RunningContextActive\">\n" + 
				"						<field-constraint field-name=\"runningContext\">\n" + 
				"							<variable-restriction evaluator=\"==\" identifier=\"ca\"/>\n" + 
				"						</field-constraint>\n" + 
				"					</pattern>\n" + 
				"				</and-conditional-element>\n" + 
				"				<pattern identifier=\"obj\" object-type=\"Predicate\">\n" + 
				"					<field-constraint field-name=\"class\">\n" + 
				"						<variable-restriction evaluator=\"==\" identifier=\"y1\"/>\n" + 
				"					</field-constraint>\n" + 
				"				</pattern>\n" + 
				"			</and-conditional-element>\n" + 
				"		</lhs>\n" + 
				"		<rhs>		insertLogical(new CountsAs(y1, y2));\n" + 
				"</rhs>\n" + 
				"	</rule>\n" + 
				"	<rule name=\"activate running context\">\n" + 
				"		<lhs>\n" + 
				"			<and-conditional-element>\n" + 
				"				<pattern identifier=\"rc\" object-type=\"RunningContext\">\n" + 
				"					<field-binding field-name=\"countsas\" identifier=\"cal\"/>\n" + 
				"				</pattern>\n" + 
				"				<forall>\n" + 
				"					<pattern identifier=\"ca\" object-type=\"ClassificatoryCountsAs\">\n" + 
				"						<field-binding field-name=\"c1\" identifier=\"a\"/>\n" + 
				"						<field-binding field-name=\"c2\" identifier=\"b\"/>\n" + 
				"						<field-binding field-name=\"c2\" identifier=\"b\"/>\n" + 
				"						<from>\n" + 
				"							<expression> cal </expression>\n" + 
				"						</from>\n" + 
				"					</pattern>\n" + 
				"					<pattern object-type=\"CountsAs\">\n" + 
				"						<field-constraint field-name=\"c1\">\n" + 
				"							<variable-restriction evaluator=\"==\" identifier=\"a\"/>\n" + 
				"						</field-constraint>\n" + 
				"						<field-constraint field-name=\"c2\">\n" + 
				"							<variable-restriction evaluator=\"==\" identifier=\"b\"/>\n" + 
				"						</field-constraint>\n" + 
				"					</pattern>\n" + 
				"				</forall>\n" + 
				"			</and-conditional-element>\n" + 
				"		</lhs>\n" + 
				"		<rhs>		insertLogical(new RunningContextActive(rc));\n" + 
				"</rhs>\n" + 
				"	</rule>\n" + 
				"	<rule name=\"activate context by its running contexts\">\n" + 
				"		<lhs>\n" + 
				"			<and-conditional-element>\n" + 
				"				<pattern identifier=\"c\" object-type=\"Context\">\n" + 
				"\n" + 
				"\n" + 
				"</pattern>\n" + 
				"				<forall>\n" + 
				"					<pattern object-type=\"RunningContextIdentifier\">\n" + 
				"						<field-binding field-name=\"runningContext\" identifier=\"rc\"/>\n" + 
				"						<field-constraint field-name=\"context\">\n" + 
				"							<variable-restriction evaluator=\"==\" identifier=\"c\"/>\n" + 
				"						</field-constraint>\n" + 
				"					</pattern>\n" + 
				"					<pattern object-type=\"RunningContextActive\">\n" + 
				"						<field-constraint field-name=\"runningContext\">\n" + 
				"							<variable-restriction evaluator=\"==\" identifier=\"rc\"/>\n" + 
				"						</field-constraint>\n" + 
				"					</pattern>\n" + 
				"				</forall>\n" + 
				"			</and-conditional-element>\n" + 
				"		</lhs>\n" + 
				"		<rhs>		insertLogical(new ContextActive(c));\n" + 
				"		System.out.println(c.getContext() + \" is active!\");\n" + 
				"</rhs>\n" + 
				"	</rule>\n" + 
				"	<rule name=\"are there any means of transport?\">\n" + 
				"		<lhs>\n" + 
				"			<pattern object-type=\"Transport\">\n" + 
				"				<field-binding field-name=\"object\" identifier=\"obj\"/>\n" + 
				"			</pattern>\n" + 
				"		</lhs>\n" + 
				"		<rhs>		System.out.println(\"We have a transport: \" + obj);\n" + 
				"</rhs>\n" + 
				"	</rule>\n" + 
				"	<rule name=\"is there any operational commander?\">\n" + 
				"		<lhs>\n" + 
				"			<pattern object-type=\"OperationalCommander\">\n" + 
				"				<field-binding field-name=\"object\" identifier=\"obj\"/>\n" + 
				"			</pattern>\n" + 
				"		</lhs>\n" + 
				"		<rhs>		System.out.println(\"We have an operational commander: \" + obj);\n" + 
				"</rhs>\n" + 
				"	</rule>\n" + 
				"</package>\n" + 
				""; 
		
		XSDRuleParser	x;
		
		x = new XSDRuleParser();
		Package p = (Package)x.deserialise(xml);
		Object ret = x.serialise(p);
		System.out.println(ret);
		System.out.println("kfjaskdfj");
		p = (Package)x.deserialise((String)ret);
		System.out.println(p);
	}
}
