package gui;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.swing.tree.TreeNode;

import apapl.APLModule;

/**
 * This class represents one node in the {@link ModuleTreeModel}. One node of
 * the module tree corresponds to one module from a multi-agent system, except
 * the root node, which does not have any corresponding module and stands for a
 * multi-agent system itself.
 * 
 * @author Michal Cap
 */
class ModuleTreeNode extends Object implements TreeNode
{
	// Module this node represents
	APLModule module;
	
	ModuleTreeNode parent;
	List<ModuleTreeNode> children;

	/**
	 * Indicates that the deliberation cycle of the module is currently being
	 * executed.
	 */
	private boolean running;

	/**
	 * Indicates that the deliberation cycle of the module is currently in
	 * sleeping mode, waiting for incoming message or external event.
	 */
	private boolean sleeping;

	/**
	 * Construct a node representing given module.
	 * 
	 * @param module the module to be associated with node
	 */
	public ModuleTreeNode(APLModule module)
	{
		this.module = module;
		this.children = new LinkedList<ModuleTreeNode>();
		this.running = false;
		this.sleeping = false;
	}

	/**
	 * Returns the module this node represents.
	 * 
	 * @return the module this node represents, <code>null</code> if this root
	 *         node that represents multi-agent system
	 */
	public APLModule getModule()
	{
		return module;
	}

	/**
	 * Returns child node on i-th position.
	 * 
	 * @param index the index of child to return
	 * @return the child node on the i-th position
	 */
	public Object getChild(int index)
	{
		return children.get(index);
	}

	/**
	 * Returns the string representation of the node. HTML tags are used to
	 * format the text in order to visualize the current execution state of the
	 * represented module.
	 */
	public String toString()
	{
		// Is it a root node?
		if (module == null)
			return "Multi-Agent System";

		if (isActive())
		{
			if (isRunning())
			{
				if (!isSleeping())
					return "<html><b>" + module.getRelativeName()
							+ "</b> \u25CF</html>";
				/* Play symbol: \u25BA */
				else
					return "<html><b>" + module.getRelativeName()
							+ "</b> <font color=#888888>\u25CF</font></html>";
			} else
			{
				return "<html><b>" + module.getRelativeName()
						+ "</b> \u25CB</html>";
			}
		} else
			return "<html>" + module.getRelativeName() + " \u25CB</html>";
	}

	/**
	 * Returns the value of <code>running</code> flag.
	 * 
	 * @param the value of <code>running</code> flag
	 */
	public boolean isRunning()
	{
		return running;
	}

	/**
	 * Returns the value of <code>sleeping</code> flag.
	 * 
	 * @param the value of <code>sleeping</code> flag
	 */
	public boolean isSleeping()
	{
		return sleeping;
	}

	public void addChild(ModuleTreeNode child)
	{
		children.add(child);
		child.setParent(this);
	}

	public ModuleTreeNode getParent()
	{
		return parent;
	}

	public void setParent(ModuleTreeNode parent)
	{
		this.parent = parent;
	}

	public void removeChild(ModuleTreeNode child)
	{
		children.remove(child);
	}

	public boolean isActive()
	{
		if (module != null)
			return module.isActive();
		else
			return false;
	}

	public int getChildCount()
	{
		return children.size();
	}

	public int getIndexOfChild(ModuleTreeNode child)
	{
		return children.indexOf(child);
	}

	/**
	 * Determines whether this node represents the multi-agent system.
	 * 
	 * @return <code>true</code> if node represents multi-agents system,
	 *         <code>false</code> if it represents a module
	 */
	public boolean isMas()
	{
		return (module == null);
	}

	/**
	 * Determines whether this node represents a module.
	 * 
	 * @return <code>true</code> if node represents a module, <code>false</code>
	 *         if it represents a multi-agent system
	 */
	public boolean isModule()
	{
		return (module != null);
	}

	/**
	 * Sets the value of <code>running</code> flag.
	 * 
	 * @param running the value to set
	 */
	public void setRunning(boolean running)
	{
		this.running = running;
	}

	/**
	 * Sets the value of <code>sleeping</code> flag.
	 * 
	 * @param sleeping the value to set
	 */
	public void setSleeping(boolean sleeping)
	{
		this.sleeping = sleeping;

	}

	public Enumeration children()
	{
		return Collections.enumeration(children);
	}

	public boolean getAllowsChildren()
	{
		return true;
	}

	public TreeNode getChildAt(int childIndex)
	{
		return children.get(childIndex);
	}

	public int getIndex(TreeNode node)
	{
		return children.indexOf(node);
	}

	public boolean isLeaf()
	{
		return (children.size() == 0);
	}
}
