package eu.superhub.wp4.monitor.core.domain;

public class HasClause {
	private Formula formula, clause;

	public void setClause(Formula clause) {
		this.clause = clause;
	}

	public Formula getClause() {
		return clause;
	}

	public void setFormula(Formula formula) {
		this.formula = formula;
	}

	public Formula getFormula() {
		return formula;
	}
}
