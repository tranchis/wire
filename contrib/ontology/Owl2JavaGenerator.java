package net.sf.ictalive.monitoring.ontology;

import de.incunabulum.owl2java.core.JenaGenerator;

public class Owl2JavaGenerator
{
	public static void main(String args[])
	{
		JenaGenerator	jg;
		
		jg = new JenaGenerator();
		jg.generate("http://ict-alive.svn.sourceforge.net/viewvc/ict-alive/OrganisationLevel/trunk/Monitoring/contrib/TMT-Onto.owl",
				"/tmp/owltest3/", "net.sf.ictalive.movies");
	}
}
