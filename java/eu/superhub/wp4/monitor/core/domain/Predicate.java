package eu.superhub.wp4.monitor.core.domain;

public class Predicate {
	protected Object object;
	private String name;

	public Predicate(String name) {
		this.setName(name);
	}

	public Predicate(Object predicate) {
		this.object = predicate;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object predicate) {
		this.object = predicate;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
