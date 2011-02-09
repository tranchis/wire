package net.sf.ictalive.monitoring.domain;

import net.sf.ictalive.monitoring.domain.Role;

public class RoleHierarchyDefinition
{
	private Role	parent, child;
	
	public RoleHierarchyDefinition(Role parent, Role child)
	{
		this.parent = parent;
		this.child = child;
	}

	public Role getParent()
	{
		return parent;
	}

	public void setParent(Role parent)
	{
		this.parent = parent;
	}

	public void setChild(Role child)
	{
		this.child = child;
	}

	public Role getChild()
	{
		return child;
	}
}
