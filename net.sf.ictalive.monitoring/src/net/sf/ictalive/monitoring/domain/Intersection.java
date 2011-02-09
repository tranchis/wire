package net.sf.ictalive.monitoring.domain;

public class Intersection
{
	public Intersection(Class<?> c1, Class<?> c2, Class<?> intersection)
	{
		this.c1 = c1;
		this.c2 = c2;
		this.intersection = intersection;
	}
	
	public Class<?> getC1() {
		return c1;
	}

	public void setC1(Class<?> c1) {
		this.c1 = c1;
	}

	public Class<?> getC2() {
		return c2;
	}

	public void setC2(Class<?> c2) {
		this.c2 = c2;
	}

	public Class<?> getIntersection() {
		return intersection;
	}

	public void setIntersection(Class<?> intersection) {
		this.intersection = intersection;
	}

	private Class<?>	c1, c2, intersection;
}
