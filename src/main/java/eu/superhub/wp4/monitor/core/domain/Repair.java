package eu.superhub.wp4.monitor.core.domain;

public class Repair {
    private Norm norm, repairNorm;

    public Norm getNorm() {
	return norm;
    }

    public void setNorm(Norm norm) {
	this.norm = norm;
    }

    public Norm getRepairNorm() {
	return repairNorm;
    }

    public void setRepairNorm(Norm repairNorm) {
	this.repairNorm = repairNorm;
    }
}
