package gui;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import apapl.APLModule;

/**
 * This class implements the tree structure representing the hierarchy of
 * modules in the multi-agent system. It implements {@link TreeModel} interface
 * and therefore can act as a data provider for the {@link JTree} component.
 * 
 * @author Michal Cap
 */
class ModuleTreeModel implements TreeModel
{
	// Root node
	private ModuleTreeNode root;
	// Specifies mapping between 2APL modules to Tree nodes
	private HashMap<APLModule, ModuleTreeNode> treeNodes = new HashMap<APLModule, ModuleTreeNode>();
	// Listeners
	private Vector<TreeModelListener> vector = new Vector<TreeModelListener>();

	public ModuleTreeModel()
	{
		super();
		// Creates a root node representing the multi-agent system
		root = new ModuleTreeNode(null);
		treeNodes.put(null, root);
	}

	public void addTreeModelListener(TreeModelListener listener)
	{
		if (listener != null && !vector.contains(listener))
		{
			vector.addElement(listener);
		}
	}

	public void removeTreeModelListener(TreeModelListener listener)
	{
		if (listener != null)
		{
			vector.removeElement(listener);
		}
	}

	public void fireTreeNodesChanged(TreeModelEvent e)
	{
		Enumeration<TreeModelListener> listeners = vector.elements();
		while (listeners.hasMoreElements())
		{
			TreeModelListener listener = (TreeModelListener) listeners
					.nextElement();
			listener.treeNodesChanged(e);
		}
	}

	public void fireTreeNodesInserted(TreeModelEvent e)
	{
		Enumeration<TreeModelListener> listeners = vector.elements();
		while (listeners.hasMoreElements())
		{
			TreeModelListener listener = (TreeModelListener) listeners
					.nextElement();
			listener.treeNodesInserted(e);
		}
	}

	public void fireTreeNodesRemoved(TreeModelEvent e)
	{
		Enumeration<TreeModelListener> listeners = vector.elements();
		while (listeners.hasMoreElements())
		{
			TreeModelListener listener = (TreeModelListener) listeners
					.nextElement();
			listener.treeNodesRemoved(e);
		}
	}

	public void fireTreeStructureChanged(TreeModelEvent e)
	{
		Enumeration<TreeModelListener> listeners = vector.elements();
		while (listeners.hasMoreElements())
		{
			TreeModelListener listener = (TreeModelListener) listeners
					.nextElement();
			listener.treeStructureChanged(e);
		}
	}

	public Object getChild(Object arg0, int arg1)
	{
		ModuleTreeNode node = (ModuleTreeNode) arg0;
		return node.getChild(arg1);
	}

	public int getChildCount(Object arg0)
	{
		ModuleTreeNode node = (ModuleTreeNode) arg0;
		return node.getChildCount();
	}

	public int getIndexOfChild(Object arg0, Object arg1)
	{
		ModuleTreeNode node = (ModuleTreeNode) arg0;
		ModuleTreeNode child = (ModuleTreeNode) arg1;
		return node.getIndexOfChild(child);

	}

	public Object getRoot()
	{
		return root;
	}

	public boolean isLeaf(Object arg0)
	{
		ModuleTreeNode node = (ModuleTreeNode) arg0;
		return (node.getChildCount() == 0);
	}

	public void valueForPathChanged(TreePath arg0, Object arg1)
	{
		// This is not used as we do not allow renaming of nodes by user
	}

	/**
	 * Adds the node representing certain module in the multi-agent system.
	 * 
	 * @param parent the parent of the child module, set to <code>null</code> if
	 *        the child is a main module
	 * @param child the module to be added
	 */
	public void addNode(APLModule parent, APLModule child)
	{
		ModuleTreeNode parentNode = treeNodes.get(parent);
		ModuleTreeNode childNode = new ModuleTreeNode(child);
		parentNode.addChild(childNode);
		treeNodes.put(child, childNode);

		// Inform the listeners about the change
		TreePath parentPath = getPathTo(parentNode);
		int[] indicies = new int[1];
		indicies[0] = parentNode.getChildCount() - 1;
		Object[] nodes = new ModuleTreeNode[1];
		nodes[0] = childNode;

		fireTreeNodesInserted(new TreeModelEvent(this, parentPath, indicies,
				nodes));
	}

	/**
	 * Removes the node representing certain module in the multi-agent system.
	 * 
	 * @param parent the parent of the removed module, set to <code>null</code>
	 *        if the child is a main module
	 * @param child the module to be removed
	 */
	public void removeNode(APLModule parent, APLModule child)
	{
		ModuleTreeNode parentNode = treeNodes.get(parent);
		ModuleTreeNode childNode = treeNodes.get(child);

		// Inform the listeners about the change
		TreePath parentPath = getPathTo(parentNode);
		int[] indicies = new int[1];
		indicies[0] = parentNode.getIndexOfChild(childNode);
		Object[] nodes = new ModuleTreeNode[1];
		nodes[0] = childNode;

		fireTreeNodesRemoved(new TreeModelEvent(this, parentPath, indicies,
				nodes));

		// Actually remove the node
		parentNode.removeChild(childNode);
		treeNodes.remove(child);
	}

	/**
	 * Changes visualization of the module's node to active.
	 * 
	 * @param module the module that has been activated
	 */
	public void activateNode(APLModule module)
	{
		// Inform the listeners about the change
		ModuleTreeNode parentNode = treeNodes.get(module.getParent());
		ModuleTreeNode childNode = treeNodes.get(module);

		// Inform the listeners about the change
		TreePath parentPath = getPathTo(parentNode);
		int[] indicies = new int[1];
		indicies[0] = parentNode.getIndex(childNode);
		Object[] nodes = new ModuleTreeNode[1];
		nodes[0] = childNode;

		fireTreeNodesChanged(new TreeModelEvent(this, parentPath, indicies,
				nodes));
	}

	/**
	 * Changes visualization of the module's node to inactive.
	 * 
	 * @param module the module that has been inactivated
	 */
	public void deactivateNode(APLModule module)
	{
		// Inform the listeners about the change
		ModuleTreeNode parentNode = treeNodes.get(module.getParent());
		ModuleTreeNode childNode = treeNodes.get(module);

		// Inform the listeners about the change
		TreePath parentPath = getPathTo(parentNode);
		int[] indicies = new int[1];
		indicies[0] = parentNode.getIndex(childNode);
		Object[] nodes = new ModuleTreeNode[1];
		nodes[0] = childNode;

		fireTreeNodesChanged(new TreeModelEvent(this, parentPath, indicies,
				nodes));
	}

	/**
	 * Sets the flag representing that the module's deliberation cycle starts
	 * being executed. Visualization of the node is changed to running.
	 * 
	 * @param module the module that has been started
	 */
	public void setStartedNode(APLModule module)
	{
		// Inform the listeners about the change
		ModuleTreeNode parentNode = treeNodes.get(module.getParent());
		ModuleTreeNode childNode = treeNodes.get(module);

		childNode.setRunning(true);

		// Inform the listeners about the change
		TreePath parentPath = getPathTo(parentNode);
		int[] indicies = new int[1];
		indicies[0] = parentNode.getIndex(childNode);
		Object[] nodes = new ModuleTreeNode[1];
		nodes[0] = childNode;

		fireTreeNodesChanged(new TreeModelEvent(this, parentPath, indicies,
				nodes));
	}

	/**
	 * Sets the flag representing that the module's deliberation cycle stopped
	 * being executed. Visualization of the node is changed to stopped.
	 * 
	 * @param module the module that has been started
	 */
	public void setStoppedNode(APLModule module)
	{
		// Inform the listeners about the change
		ModuleTreeNode parentNode = treeNodes.get(module.getParent());
		ModuleTreeNode childNode = treeNodes.get(module);

		childNode.setRunning(false);

		// Inform the listeners about the change
		TreePath parentPath = getPathTo(parentNode);
		int[] indicies = new int[1];
		indicies[0] = parentNode.getIndex(childNode);
		Object[] nodes = new ModuleTreeNode[1];
		nodes[0] = childNode;

		fireTreeNodesChanged(new TreeModelEvent(this, parentPath, indicies,
				nodes));
	}

	public TreePath getPathTo(ModuleTreeNode node)
	{
		List<ModuleTreeNode> nodes = new LinkedList<ModuleTreeNode>();
		ModuleTreeNode currentNode = node;

		while (currentNode != null)
		{
			nodes.add(0, currentNode);
			currentNode = currentNode.getParent();
		}

		return new TreePath(nodes.toArray());
	}

	/**
	 * Returns the tree node that represents given module.
	 * 
	 * @param module the module
	 * @return the node representing the module
	 */
	public ModuleTreeNode getNode(APLModule module)
	{
		return treeNodes.get(module);
	}

	/**
	 * Sets the flag representing that the module has been put to sleep. Graphic
	 * representation of the node is changed to visualize sleeping state of the
	 * module.
	 * 
	 * @param module module that has been put to sleep
	 */
	public void setNodePutToSleep(APLModule module)
	{
		// Inform the listeners about the change
		ModuleTreeNode parentNode = treeNodes.get(module.getParent());
		ModuleTreeNode childNode = treeNodes.get(module);

		childNode.setSleeping(true);

		// Inform the listeners about the change
		TreePath parentPath = getPathTo(parentNode);
		int[] indicies = new int[1];
		indicies[0] = parentNode.getIndex(childNode);
		Object[] nodes = new ModuleTreeNode[1];
		nodes[0] = childNode;

		fireTreeNodesChanged(new TreeModelEvent(this, parentPath, indicies,
				nodes));
	}

	/**
	 * Sets the flag representing that the module has been waken up. Graphic
	 * representation of the node is changed to visualize running state of the
	 * module.
	 * 
	 * @param module module that has been waken up
	 */
	public void setNodeWakenUp(APLModule module)
	{
		// Inform the listeners about the change
		ModuleTreeNode parentNode = treeNodes.get(module.getParent());
		ModuleTreeNode childNode = treeNodes.get(module);

		childNode.setSleeping(false);

		// Inform the listeners about the change
		TreePath parentPath = getPathTo(parentNode);
		int[] indicies = new int[1];
		indicies[0] = parentNode.getIndex(childNode);
		Object[] nodes = new ModuleTreeNode[1];
		nodes[0] = childNode;

		fireTreeNodesChanged(new TreeModelEvent(this, parentPath, indicies,
				nodes));
	}
}
