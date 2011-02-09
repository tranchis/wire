package net.sf.ictalive.monitoring.rules.drools;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Vector;

import net.sf.ictalive.metamodel.utils.Serialiser;
import net.sf.ictalive.operetta.OM.CountsAs;
import net.sf.ictalive.operetta.OM.Norm;
import net.sf.ictalive.operetta.OM.OMPackage;
import net.sf.ictalive.operetta.OM.OperAModel;

public class Opera2Drools
{
	private ParsedNorms	pn;
	private OperAModel	om;
	
	public Opera2Drools(String file) throws IOException
	{	
		Serialiser<OperAModel>	s;

		s = new Serialiser<OperAModel>(OMPackage.class, "opera", false);
		om = s.deserialise(new File(DroolsEngine.class.getClassLoader().getResource(file).getFile().replace("%20", " ")));	
	}
	
	public Opera2Drools(OperAModel om)
	{
		this.om = om;
	}
	
	public void parse() throws IOException
	{
		Collection<Norm>		ns;
		Collection<CountsAs>	cs;
		NormParser				np;
		Vector<Norm>			vn;
		
		ns = om.getOm().getNs().getNorms();
		cs = om.getOm().getCs().getCountsAsRules();
		np = new NormParser();
		
		vn = new Vector<Norm>();
		vn.add(ns.iterator().next());
		
		pn = np.parseToPackage("prueba", ns, cs);
	}
	
	public ParsedNorms toDrools()
	{
		return pn;
	}
}
