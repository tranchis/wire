package eu.superhub.wp4.monitor.core.domain;

import clojure.lang.Obj;

public class Norm extends ConditionHolder {
	public int normType;
	Obj normActivation;
	Obj normCondition;
	public Obj normExpiration;
	public String normTarget;
	private String normID;

	public final static int OBLIGATION = 0;
	public final static int PERMISSION = 1;
	public final static int PROHIBITION = 2;

	public Norm(String normID, Obj normActivation, Obj normCondition,
			Obj normExpiration) {
		this.normID = normID;
		this.normActivation = normActivation;
		this.normCondition = normCondition;
		this.normExpiration = normExpiration;
	}

	public int getNormType() {
		return normType;
	}

	public void setNormType(int normType) {
		this.normType = normType;
	}

	public Obj getNormActivation() {
		return normActivation;
	}

	public void setNormActivation(Obj normActivation) {
		this.normActivation = normActivation;
	}

	public Obj getNormMaintenance() {
		return normCondition;
	}

	public void setNormMaintenance(Obj normCondition) {
		this.normCondition = normCondition;
	}

	public Obj getNormExpiration() {
		return normExpiration;
	}

	public void setNormExpiration(Obj normExpiration) {
		this.normExpiration = normExpiration;
	}

	public String getNormTarget() {
		return normTarget;
	}

	public void setNormTarget(String normTarget) {
		this.normTarget = normTarget;
	}

	public String getNormID() {
		return normID;
	}

	public void setNormID(String normID) {
		this.normID = normID;
	}

	@Override
	public String getID() {
		return normID;
	}

	@Override
	public Obj getCondition(int mode) {
		Obj res;

		if (mode == ConditionHolder.ACTIVATION) {
			res = normActivation;
		} else if (mode == ConditionHolder.MAINTENANCE) {
			res = normCondition;
		} else if (mode == ConditionHolder.DEACTIVATION) {
			res = normExpiration;
		} else {
			throw new UnsupportedOperationException();
		}

		return res;
	}

	// public void setOriginalNorm(net.sf.ictalive.operetta.OM.Norm
	// originalNorm)
	// {
	// this.originalNorm = originalNorm;
	// }

	public net.sf.ictalive.operetta.OM.Norm getOriginalNorm() {
		net.sf.ictalive.operetta.OM.Norm n;

		n = net.sf.ictalive.operetta.OM.OMFactory.eINSTANCE.createNorm();
		n.setNormID(this.normID);

		return n;
	}
}
