package net.sf.ictalive.monitoring.rules.drools;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.xml.bind.JAXBElement;

import net.sf.ictalive.monitoring.domain.ConditionHolder;
import net.sf.ictalive.monitoring.rules.drools.schema.AndConditionalElement;
import net.sf.ictalive.monitoring.rules.drools.schema.ConditionalElementType;
import net.sf.ictalive.monitoring.rules.drools.schema.FieldBinding;
import net.sf.ictalive.monitoring.rules.drools.schema.FieldConstraint;
import net.sf.ictalive.monitoring.rules.drools.schema.Lhs;
import net.sf.ictalive.monitoring.rules.drools.schema.LiteralRestriction;
import net.sf.ictalive.monitoring.rules.drools.schema.Not;
import net.sf.ictalive.monitoring.rules.drools.schema.ObjectFactory;
import net.sf.ictalive.monitoring.rules.drools.schema.OrConditionalElement;
import net.sf.ictalive.monitoring.rules.drools.schema.Pattern;
import net.sf.ictalive.monitoring.rules.drools.schema.Rule;
import net.sf.ictalive.monitoring.rules.drools.schema.VariableRestriction;
import net.sf.ictalive.operetta.OM.Atom;
import net.sf.ictalive.operetta.OM.Conjunction;
import net.sf.ictalive.operetta.OM.Constant;
import net.sf.ictalive.operetta.OM.Disjunction;
import net.sf.ictalive.operetta.OM.Function;
import net.sf.ictalive.operetta.OM.Implication;
import net.sf.ictalive.operetta.OM.Negation;
import net.sf.ictalive.operetta.OM.OMFactory;
import net.sf.ictalive.operetta.OM.PartialStateDescription;
import net.sf.ictalive.operetta.OM.PathNegation;
import net.sf.ictalive.operetta.OM.Term;
import net.sf.ictalive.operetta.OM.Variable;

public class ConditionParser
{
	private static Map<String,Set<String>>	map;
	protected ObjectFactory					of;
	
	static
	{
		map = new TreeMap<String,Set<String>>();
	}
	
	public ConditionParser()
	{
		of = new ObjectFactory();
	}
	
	public Collection<Rule> parseCondition(ConditionHolder ch, int mode)
	{
		Object					res;
		AndConditionalElement[]	clauses;
		Vector<Rule>			rules;
		int						i;
		
		rules = new Vector<Rule>();
		
		res = parseFormula(ch.getCondition(mode));
		clauses = normaliseFormula(res).getAndConditionalElement().toArray(new AndConditionalElement[0]);
		
		for(i=0;i<clauses.length;i++)
		{
			rules.add(createRuleFromClause(ch, clauses[i], mode, i));
		}
		
		return rules;
	}
	
	protected Rule createRuleFromClause(ConditionHolder ch, AndConditionalElement and, int mode, int i)
	{
		Rule					r;
		Lhs						lhs;
		Set<String>				vars;
		String					rhs, ruleName, key, cl;
		Iterator<String>		it;
		
		cl = ch.getClass().getSimpleName();
		ruleName = cl + ":" + ch.getID() + "_" + ch.getType(mode) + "_" + i;

		r = new Rule();
		r.setName(ruleName);
		
		lhs = new Lhs();
		
		if(cl.equals("Norm"))
		{
			vars = map.get(ch.getID());
			
			if(vars == null)
			{
				vars = new TreeSet<String>();
			}
			else
			{
				vars = new TreeSet<String>(vars);
				createPatternsForValues(lhs, vars);
			}
		}
		else if(cl.equals("CountsAs"))
		{
			vars = new TreeSet<String>();
		}
		else
		{
			throw new UnsupportedOperationException();
		}
		
		and = fixVariables(and, vars);
	
		if(cl.equals("Norm"))
		{
			createPatternsNorm(lhs, ch.getType(mode), ch.getID());
		}
		else if(cl.equals("CountsAs"))
		{
			addPatternsCountsAs(lhs, ch.getType(mode), ch.getID());
		}
		else
		{
			throw new UnsupportedOperationException();
		}
		
		lhs.getAbstractConditionalElementOrNotOrExists().add(of.createAndConditionalElement(and));
		
		r.setLhs(lhs);
		
		rhs = "\t\tSubstitution theta = new Substitution();\n";
		rhs = rhs + "\t\tValue value;\n";
		
		it = vars.iterator();
		while(it.hasNext())
		{
			key = it.next();
			rhs = rhs + "\t\tvalue = Value.createValue(\"" + key + "\", " + key + ");\n";
			rhs = rhs + "\t\tinsertLogical(value);\n";
			rhs = rhs + "\t\ttheta.add(value);\n";
		}
		rhs = rhs + "\n\t\ttheta = Substitution.getSubstitution(theta);\n";
		rhs = rhs + "\t\tinsertLogical(theta);\n";
		
		rhs = rhs + "\t\tinsertLogical(new Holds";
		rhs = rhs + "(f, theta));\n";
		rhs = rhs + "System.out.println(\"" + ch.getID() + "_" + ch.getType(mode) + " fired with substitution: [\" + theta + \"]\");";
		r.setRhs(rhs);
		
		if(cl.equals("Norm") && ch.getType(mode).equals("activation"))
		{
			map.put(ch.getID(), vars);
		}
		
		return r;
	}

	private void createPatternsForValues(Lhs lhs, Set<String> vars)
	{
		String					key;
		Iterator<String>		it;
		Pattern					p;
		FieldConstraint			fc;
		LiteralRestriction		lr;
		FieldBinding			fb;

		it = vars.iterator();
		while(it.hasNext())
		{
			key = it.next();
			p = new Pattern();
			p.setObjectType("Value");
			fc = new FieldConstraint();
			fc.setFieldName("key");
			lr = new LiteralRestriction();
			lr.setEvaluator("==");
			lr.setValue(key);
			fc.getLiteralRestrictionOrVariableRestrictionOrReturnValueRestriction().add(lr);
			p.getFieldBindingOrFieldConstraintOrFrom().add(fc);
			fb = new FieldBinding();
			fb.setFieldName("value");
			fb.setIdentifier(key);
			p.getFieldBindingOrFieldConstraintOrFrom().add(fb);
			lhs.getAbstractConditionalElementOrNotOrExists().add(p);
		}
	}

	private void addPatternsCountsAs(Lhs lhs, String type, String code)
	{
		Pattern					p;
		FieldBinding			fb;
		FieldConstraint			fc;
		LiteralRestriction		lr;

		p = new Pattern();
		p.setObjectType("CountsAs");
		fb = new FieldBinding();
		fb.setFieldName(type);
		fb.setIdentifier("f");
		p.getFieldBindingOrFieldConstraintOrFrom().add(fb);
		fc = new FieldConstraint();
		fc.setFieldName("id");
		lr = new LiteralRestriction();
		lr.setEvaluator("==");
		lr.setValue(code);
		fc.getLiteralRestrictionOrVariableRestrictionOrReturnValueRestriction().add(lr);
		p.getFieldBindingOrFieldConstraintOrFrom().add(fc);
		lhs.getAbstractConditionalElementOrNotOrExists().add(p);
	}

	private void createPatternsNorm(Lhs lhs, String type, String code)
	{
		Pattern					p;
		FieldConstraint			fc;
		LiteralRestriction		lr;
		FieldBinding			fb;
		VariableRestriction		vr;

		p = new Pattern();
		p.setIdentifier("rulenorm");
		p.setObjectType("Norm");
		fc = new FieldConstraint();
		fc.setFieldName("normID");
		lr = new LiteralRestriction();
		lr.setEvaluator("==");
		lr.setValue(code);
		fc.getLiteralRestrictionOrVariableRestrictionOrReturnValueRestriction().add(lr);
		p.getFieldBindingOrFieldConstraintOrFrom().add(fc);
		lhs.getAbstractConditionalElementOrNotOrExists().add(p);
		
		p = new Pattern();
		p.setObjectType(type.substring(0, 1).toUpperCase() + type.substring(1));
		fc = new FieldConstraint();
		fc.setFieldName("norm");
		vr = new VariableRestriction();
		vr.setEvaluator("==");
		vr.setIdentifier("rulenorm");
		fc.getLiteralRestrictionOrVariableRestrictionOrReturnValueRestriction().add(vr);
		p.getFieldBindingOrFieldConstraintOrFrom().add(fc);
		fb = new FieldBinding();
		fb.setFieldName("formula");
		fb.setIdentifier("f");
		p.getFieldBindingOrFieldConstraintOrFrom().add(fb);
		lhs.getAbstractConditionalElementOrNotOrExists().add(p);
	}

	private AndConditionalElement fixVariables(AndConditionalElement and, Set<String> context)
	{
		Object				obj, field, restriction;
		Pattern				p, q;
		int					i, j, k;
		FieldConstraint		fc;
		VariableRestriction	vr;
		FieldBinding		fb;
		
		for(i=0;i<and.getNotOrExistsOrEval().size();i++)
		{
			obj = and.getNotOrExistsOrEval().get(i);
//			if(obj instanceof Not)
//			{
//				q = (Pattern)((Not)obj).getAbstractConditionalElementOrNotOrExists().get(0);
//			}
//			else
//			{
			if(obj instanceof Pattern)
			{
				q = (Pattern)obj;
				
				p = copy(q);
				
				for(j=0;j<p.getFieldBindingOrFieldConstraintOrFrom().size();j++)
				{
					field = p.getFieldBindingOrFieldConstraintOrFrom().get(j);
					if(field instanceof FieldConstraint)
					{
						fc = (FieldConstraint)field;
						for(k=0;k<fc.getLiteralRestrictionOrVariableRestrictionOrReturnValueRestriction().size();k++)
						{
							restriction = fc.getLiteralRestrictionOrVariableRestrictionOrReturnValueRestriction().get(k);
							if(restriction instanceof VariableRestriction)
							{
								vr = (VariableRestriction)restriction;
								if(!context.contains(vr.getIdentifier())) // TODO: Pending fix
								{
									fb = new FieldBinding();
									fb.setFieldName(fc.getFieldName());
									fb.setIdentifier(vr.getIdentifier()); // TODO: Pending fix
									p.getFieldBindingOrFieldConstraintOrFrom().add(fb);
									p.getFieldBindingOrFieldConstraintOrFrom().remove(fc);
									context.add(vr.getIdentifier()); // TODO: Pending fix
								}
							}
						}
					}
				}
				
//				if(obj instanceof Not)
//				{
//					((Not)obj).getAbstractConditionalElementOrNotOrExists().remove(q);
//					((Not)obj).getAbstractConditionalElementOrNotOrExists().add(i, p);
//				}
//				else
//				{
					and.getNotOrExistsOrEval().remove(q);
					and.getNotOrExistsOrEval().add(i, p);
//				}
			}
		}
		
		return and;
	}

	private Pattern copy(Pattern s)
	{
		Pattern		p;
		
		p = new Pattern();
		p.setFieldName(s.getFieldName());
		p.setIdentifier(s.getIdentifier());
		p.setObjectType(s.getObjectType());
		p.getFieldBindingOrFieldConstraintOrFrom().addAll(s.getFieldBindingOrFieldConstraintOrFrom());
		
		return p;
	}

	protected OrConditionalElement normaliseFormula(Object formula)
	{
		Object[]				res, je;
		OrConditionalElement	or;
		int						i, j;
		AndConditionalElement	and;
		
		res = convertToDNF(cleanNegations(formula));
		or = of.createOrConditionalElement();
		for(i=0;i<res.length;i++)
		{
			je = (Object[])res[i];
			and = new AndConditionalElement();
			for(j=0;j<je.length;j++)
			{
				and.getNotOrExistsOrEval().add(je[j]);
			}
			or.getAndConditionalElement().add(and);
		}
		
		return or;
	}

	private Object[] convertToDNF(AndConditionalElement and)
	{
		Object[]						res, je;
		int								i, j, k;
		Vector<Vector<Object>>			vector, vectorOld;
		Iterator<OrConditionalElement>	it;
		Vector<Object>					fixed;
		Iterator<Object>				objs;
		Vector<Object>					set;

		// Fixed parts
		fixed = new Vector<Object>();
		objs = and.getNotOrExistsOrEval().iterator();
		while(objs.hasNext())
		{
			res = convertToDNF(objs.next());
			for(i=0;i<res.length;i++)
			{
				je = (Object[])res[i];
				for(j=0;j<je.length;j++)
				{
					fixed.add(je[j]);
				}
			}
		}
		
		// Conditionals: distribution
		vector = new Vector<Vector<Object>>();
		objs = fixed.iterator();
		set = new Vector<Object>();
		while(objs.hasNext())
		{
			set.add(objs.next());
		}
		vector.add(set);
		it = and.getOrConditionalElement().iterator();
		while(it.hasNext())
		{
			res = convertToDNF(it.next());
			vectorOld = vector;
			vector = new Vector<Vector<Object>>();
			for(i=0;i<vectorOld.size();i++)
			{
				for(j=0;j<res.length;j++)
				{
					set = new Vector<Object>(vectorOld.get(i));
					je = (Object[])res[j];
					for(k=0;k<je.length;k++)
					{
						set.add(je[k]);
					}					
					vector.add(set);
				}
			}
		}
		
		res = new Object[vector.size()];
		for(i=0;i<res.length;i++)
		{
			set = vector.get(i);
			res[i] = set.toArray();
		}
		
		return res;
	}
	
	private Object[] convertToDNF(OrConditionalElement or)
	{
		Object[]				res;
		int						i, j;
		Vector<Object>			fixed;

		fixed = new Vector<Object>();
		for(i=0;i<or.getAndConditionalElement().size();i++)
		{
			res = convertToDNF(or.getAndConditionalElement().get(i));
			for(j=0;j<res.length;j++)
			{
				fixed.add(res[j]);
			}
		}
		for(i=0;i<or.getNotOrExistsOrEval().size();i++)
		{
			res = convertToDNF(or.getNotOrExistsOrEval().get(i));
			for(j=0;j<res.length;j++)
			{
				fixed.add(res[j]);
			}
		}
		
		res = fixed.toArray();

		return res;
	}
	
	protected Object[] convertToDNF(Object formula)
	{
		Object[]	res;
		Object		obj;
		
		if(formula instanceof Pattern || formula instanceof Not)
		{
			res = new Object[] { new Object[] { formula } };
		}
		else if(formula instanceof OrConditionalElement)
		{
			res = convertToDNF((OrConditionalElement)formula);
		}
		else if(formula instanceof AndConditionalElement)
		{
			res = convertToDNF((AndConditionalElement)formula);			
		}
		else if(formula instanceof JAXBElement<?>)
		{
			obj = ((JAXBElement<?>)formula).getValue();
			if(obj instanceof AndConditionalElement)
			{
				res = convertToDNF((AndConditionalElement)obj);
			}
			else if(obj instanceof OrConditionalElement)
			{
				res = convertToDNF((OrConditionalElement)obj);
			}
			else
			{
				throw new UnsupportedOperationException(obj.getClass().getName());
			}
		}
		else
		{
			throw new UnsupportedOperationException(formula.getClass().getName());
		}
		
		return res;
	}

	protected Object cleanNegations(Object formula)
	{
		Object					res;
		Not						not;
		List<Object>			lo;
		Object[]				oo;
		int						i;
		Object					no;
		OrConditionalElement	or;
		
		if(formula instanceof Not)
		{
			not = (Not)formula;
			no = not.getAbstractConditionalElementOrNotOrExists().get(0);
			while(no instanceof Not)
			{
				no = ((Not)no).getAbstractConditionalElementOrNotOrExists().get(0);
			}
			not.getAbstractConditionalElementOrNotOrExists().remove(0);
			not.getAbstractConditionalElementOrNotOrExists().add(cleanNegations(no));
			
			res = formula;
		}
		else if(formula instanceof JAXBElement<?>)
		{
			lo = ((ConditionalElementType)((JAXBElement<?>)formula).getValue()).getNotOrExistsOrEval();
			oo = lo.toArray();
			
			for(i=0;i<oo.length;i++)
			{
				res = oo[i];
				lo.remove(res);
				lo.add(cleanNegations(res));
			}
			
			res = formula;
		}
		else if(formula instanceof OrConditionalElement)
		{
			or = (OrConditionalElement)formula;
			lo = or.getNotOrExistsOrEval();
			oo = lo.toArray();
			
			for(i=0;i<oo.length;i++)
			{
				res = oo[i];
				lo.remove(res);
				lo.add(cleanNegations(res));
			}
			
			res = formula;
		}
		else if(formula instanceof Pattern)
		{
			res = formula;
		}
		else
		{
			throw new UnsupportedOperationException(formula.getClass().getName());
		}
		
		return res;
	}

	private Object parseFormula(Atom a)
	{
		Pattern					p;
		FieldConstraint			fc;
		int						i;
		LiteralRestriction		lr;
		Iterator<Term>			terms;
		Term					t;
		
		p = new Pattern();
		p.setIdentifier("id" + new Random().nextInt(10000));
		p.setObjectType("Proposition");
		
		fc = new FieldConstraint();
		fc.setFieldName("predicate");
		lr = new LiteralRestriction();
		lr.setEvaluator("==");
		lr.setValue(a.getPredicate());
		fc.getLiteralRestrictionOrVariableRestrictionOrReturnValueRestriction().add(lr);
		p.getFieldBindingOrFieldConstraintOrFrom().add(fc);
		
		i = 0;
		terms = a.getArguments().iterator();
		while(terms.hasNext())
		{
			t = terms.next();
			p.getFieldBindingOrFieldConstraintOrFrom().add(parseTerm(t, i));
			i++;
		}
		
		return p;
	}

	private Object parseTerm(Variable v, int i)
	{
		FieldConstraint			fc;
		String					name;
		VariableRestriction		vr;
		Object					res;

		name = v.getName();

		fc = new FieldConstraint();
		fc.setFieldName("p" + i);
		vr = new VariableRestriction();
		vr.setEvaluator("==");
		vr.setIdentifier(name);
		fc.getLiteralRestrictionOrVariableRestrictionOrReturnValueRestriction().add(vr);
		res = fc;
		
		return res;
	}
	
	private Object parseTerm(Constant ct, int i)
	{
		FieldConstraint			fc;
		LiteralRestriction		lr;
		String					content;

		content = ct.getName();
		
		if(content.charAt(0) == '"')
		{
			content = content.substring(1, content.length() - 1);
		}
		
		fc = new FieldConstraint();
		fc.setFieldName("p" + i);
		lr = new LiteralRestriction();
		lr.setEvaluator("==");
		lr.setValue(content);
		fc.getLiteralRestrictionOrVariableRestrictionOrReturnValueRestriction().add(lr);
		
		return fc;
	}
	
	private Object parseFunction(Function ct, int i)
	{
		FieldConstraint			fc;
		LiteralRestriction		lr;
		String					content;

		content = ct.getName();
		
		if(content.charAt(0) == '"')
		{
			content = content.substring(1, content.length() - 1);
		}
		
		fc = new FieldConstraint();
		fc.setFieldName("p" + i);
		lr = new LiteralRestriction();
		lr.setEvaluator("==");
		lr.setValue(content);
		fc.getLiteralRestrictionOrVariableRestrictionOrReturnValueRestriction().add(lr);
		
		return fc;
	}
	
	private Object parseTerm(Term t, int i)
	{
		Object					res;

		if(t instanceof Variable)
		{
			res = parseTerm((Variable)t, i);
		}
		else if(t instanceof Function)
		{
			res = parseFunction((Function)t, i);
		}
		else // t is a Constant
		{
			res = parseTerm((Constant)t, i);
		}
		
		return res;
	}
	
	private Object parseFormula(Negation n)
	{
		Object					res, tmp;
		
		tmp = parseFormula(n.getStateFormula());
		res = negateFormula(tmp);

		return res;
	}
	
	private Object parseFormula(PathNegation n)
	{
		Object					res, tmp;
		
		tmp = parseFormula(n.getPathFormula());
		res = negateFormula(tmp);

		return res;
	}
	
	private Object negateFormula(Object formula)
	{
		Object							res;
		Not								not;
		JAXBElement<?>					je;
		OrConditionalElement			or;
		AndConditionalElement			and;
		Iterator<Object>				it;
		Iterator<OrConditionalElement>	ors;
		Iterator<AndConditionalElement>	ands;
		
		if(formula instanceof Pattern)
		{
			not = new Not();
			not.getAbstractConditionalElementOrNotOrExists().add(formula);
			res = not;
		}
		else if(formula instanceof Not)
		{
			res = ((Not)formula).getAbstractConditionalElementOrNotOrExists().get(0);
		}
		else if(formula instanceof OrConditionalElement)
		{
			res = negateFormula(of.createOrConditionalElement((OrConditionalElement)formula));
		}
		else if(formula instanceof AndConditionalElement)
		{
			res = negateFormula(of.createAndConditionalElement((AndConditionalElement)formula));
		}
		else if(formula instanceof JAXBElement<?>)
		{
			je = (JAXBElement<?>)formula;
			if(je.getValue() instanceof AndConditionalElement)
			{
				and = (AndConditionalElement)je.getValue();
				or = new OrConditionalElement();
				
				it = and.getNotOrExistsOrEval().iterator();
				while(it.hasNext())
				{
					or.getNotOrExistsOrEval().add(negateFormula(it.next()));
				}
				
				ors = and.getOrConditionalElement().iterator();
				while(ors.hasNext())
				{
					or.getAndConditionalElement().add((AndConditionalElement)((JAXBElement<?>)negateFormula(ors.next())).getValue());
				}
				
				res = of.createOrConditionalElement(or);
			}
			else if(je.getValue() instanceof OrConditionalElement)
			{
				or = (OrConditionalElement)je.getValue();
				and = new AndConditionalElement();
				
				it = or.getNotOrExistsOrEval().iterator();
				while(it.hasNext())
				{
					and.getNotOrExistsOrEval().add(negateFormula(it.next()));
				}
				
				ands = or.getAndConditionalElement().iterator();
				while(ands.hasNext())
				{
					and.getOrConditionalElement().add((OrConditionalElement)((JAXBElement<?>)negateFormula(ands.next())).getValue());
				}
				
				res = of.createAndConditionalElement(and);
			}
			else
			{
				throw new UnsupportedOperationException(je.getValue().getClass().getName());
			}
		}
		else
		{
			throw new UnsupportedOperationException(formula.getClass().getName());
		}
		
		return res;
	}

	private Object parseFormula(Conjunction c)
	{
		Object					tmp;
		AndConditionalElement	and;
		OrConditionalElement	or;

		and = new AndConditionalElement();

		tmp = parseFormula(c.getLeftStateFormula());
		if(tmp != null)
		{
			if(tmp instanceof AndConditionalElement)
			{
				or = of.createOrConditionalElement();
				or.getAndConditionalElement().add((AndConditionalElement)tmp);
				and.getOrConditionalElement().add(or);
			}
			else if(tmp instanceof OrConditionalElement)
			{
				and.getOrConditionalElement().add((OrConditionalElement)tmp);
			}
			else if(tmp instanceof JAXBElement<?>)
			{
				tmp = ((JAXBElement<?>)tmp).getValue();
				if(tmp instanceof AndConditionalElement)
				{
					or = of.createOrConditionalElement();
					or.getAndConditionalElement().add((AndConditionalElement)tmp);
					and.getOrConditionalElement().add(or);
				}
				else if(tmp instanceof OrConditionalElement)
				{
					and.getOrConditionalElement().add((OrConditionalElement)tmp);
				}
			}
			else
			{
				and.getNotOrExistsOrEval().add(tmp);
			}
		}
		tmp = parseFormula(c.getRightStateFormula());
		if(tmp != null)
		{
			if(tmp instanceof AndConditionalElement)
			{
				or = new OrConditionalElement();
				or.getAndConditionalElement().add((AndConditionalElement)tmp);
				and.getOrConditionalElement().add(or);
			}				
			else if(tmp instanceof OrConditionalElement)
			{
				and.getOrConditionalElement().add((OrConditionalElement)tmp);
			}
			else if(tmp instanceof JAXBElement<?>)
			{
				tmp = ((JAXBElement<?>)tmp).getValue();
				if(tmp instanceof AndConditionalElement)
				{
					or = of.createOrConditionalElement();
					or.getAndConditionalElement().add((AndConditionalElement)tmp);
					and.getOrConditionalElement().add(or);
				}
				else if(tmp instanceof OrConditionalElement)
				{
					and.getOrConditionalElement().add((OrConditionalElement)tmp);
				}
			}
			else
			{
				and.getNotOrExistsOrEval().add(tmp);
			}
		}
		
		return of.createAndConditionalElement(and);
	}
	
	private Object parseFormula(Disjunction d)
	{
		Object					tmp;
		AndConditionalElement	and;
		OrConditionalElement	or;
		
		or = new OrConditionalElement();

		tmp = parseFormula(d.getLeftStateFormula());
		if(tmp != null)
		{
			if(tmp instanceof OrConditionalElement)
			{
				and = of.createAndConditionalElement();
				and.getOrConditionalElement().add((OrConditionalElement)tmp);
				or.getAndConditionalElement().add(and);
			}
			else if(tmp instanceof AndConditionalElement)
			{
				or.getAndConditionalElement().add((AndConditionalElement)tmp);
			}
			else if(tmp instanceof JAXBElement<?>)
			{
				tmp = ((JAXBElement<?>)tmp).getValue();
				if(tmp instanceof OrConditionalElement)
				{
					and = of.createAndConditionalElement();
					and.getOrConditionalElement().add((OrConditionalElement)tmp);
					or.getAndConditionalElement().add(and);
				}
				else if(tmp instanceof AndConditionalElement)
				{
					or.getAndConditionalElement().add((AndConditionalElement)tmp);
				}
			}
			else
			{
				or.getNotOrExistsOrEval().add(tmp);
			}
		}
		tmp = parseFormula(d.getRightStateFormula());
		if(tmp != null)
		{
			if(tmp instanceof OrConditionalElement)
			{
				and = of.createAndConditionalElement();
				and.getOrConditionalElement().add((OrConditionalElement)tmp);
				or.getAndConditionalElement().add(and);
			}
			else if(tmp instanceof AndConditionalElement)
			{
				or.getAndConditionalElement().add((AndConditionalElement)tmp);
			}
			else if(tmp instanceof JAXBElement<?>)
			{
				tmp = ((JAXBElement<?>)tmp).getValue();
				if(tmp instanceof OrConditionalElement)
				{
					and = of.createAndConditionalElement();
					and.getOrConditionalElement().add((OrConditionalElement)tmp);
					or.getAndConditionalElement().add(and);
				}
				else if(tmp instanceof AndConditionalElement)
				{
					or.getAndConditionalElement().add((AndConditionalElement)tmp);
				}
			}
			else
			{
				or.getNotOrExistsOrEval().add(tmp);
			}
		}
		
		return or;
	}

	private Object parseFormula(Implication d)
	{
		Object					tmp;
		AndConditionalElement	and;
		OrConditionalElement	or;
		Negation				neg;
		
		or = new OrConditionalElement();

		neg = OMFactory.eINSTANCE.createNegation();
		neg.setStateFormula(d.getAntecedentStateFormula());
		tmp = parseFormula(neg);
		if(tmp != null)
		{
			if(tmp instanceof OrConditionalElement)
			{
				and = of.createAndConditionalElement();
				and.getOrConditionalElement().add((OrConditionalElement)tmp);
				or.getAndConditionalElement().add(and);
			}
			else if(tmp instanceof AndConditionalElement)
			{
				or.getAndConditionalElement().add((AndConditionalElement)tmp);
			}
			else if(tmp instanceof JAXBElement<?>)
			{
				tmp = ((JAXBElement<?>)tmp).getValue();
				if(tmp instanceof OrConditionalElement)
				{
					and = of.createAndConditionalElement();
					and.getOrConditionalElement().add((OrConditionalElement)tmp);
					or.getAndConditionalElement().add(and);
				}
				else if(tmp instanceof AndConditionalElement)
				{
					or.getAndConditionalElement().add((AndConditionalElement)tmp);
				}
			}
			else
			{
				or.getNotOrExistsOrEval().add(tmp);
			}
		}
		tmp = parseFormula(d.getConsequentStateFormula());
		if(tmp != null)
		{
			if(tmp instanceof OrConditionalElement)
			{
				and = of.createAndConditionalElement();
				and.getOrConditionalElement().add((OrConditionalElement)tmp);
				or.getAndConditionalElement().add(and);
			}
			else if(tmp instanceof AndConditionalElement)
			{
				or.getAndConditionalElement().add((AndConditionalElement)tmp);
			}
			else if(tmp instanceof JAXBElement<?>)
			{
				tmp = ((JAXBElement<?>)tmp).getValue();
				if(tmp instanceof OrConditionalElement)
				{
					and = of.createAndConditionalElement();
					and.getOrConditionalElement().add((OrConditionalElement)tmp);
					or.getAndConditionalElement().add(and);
				}
				else if(tmp instanceof AndConditionalElement)
				{
					or.getAndConditionalElement().add((AndConditionalElement)tmp);
				}
			}
			else
			{
				or.getNotOrExistsOrEval().add(tmp);
			}
		}
		
		return or;
	}

	protected Object parseFormula(PartialStateDescription condition)
	{
		Object					res;
		
		res = null;
		of = new ObjectFactory();
		if(condition == null)
		{
			res = null;
		}
		else if(condition instanceof Atom)
		{
			res = parseFormula((Atom)condition);
		}
		else if(condition instanceof Negation)
		{
			res = parseFormula((Negation)condition);
		}
		else if(condition instanceof PathNegation)
		{
			res = parseFormula((PathNegation)condition);
		}
		else if(condition instanceof Conjunction)
		{
			res = parseFormula((Conjunction)condition);
		}
		else if(condition instanceof Disjunction)
		{
			res = parseFormula((Disjunction)condition);
		}
		else if(condition instanceof Implication)
		{
			res = parseFormula((Implication)condition);
		}
		else
		{
			throw new UnsupportedOperationException(condition.getClass().getName());
		}
		
		return res;
	}
}
