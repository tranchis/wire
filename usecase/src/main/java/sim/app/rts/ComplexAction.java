package sim.app.rts;

import java.util.LinkedList;
import java.util.Queue;

import sim.engine.SimState;

public abstract class ComplexAction implements Action
{
	public abstract Action[] getActionList();
	
	Queue<Action>	actions;
	Action			currentAction;
	
	private void initialize()
	{
		int			i;
		Action[]	actionList;

		actionList = getActionList();
		actions = new LinkedList<Action>();
		for(i=0;i<actionList.length;i++)
		{
			actions.add(actionList[i]);
		}
		currentAction = actions.poll();
	}
	
	public boolean isFinished()
	{
//		System.out.println("currentAction: " + currentAction.getClass());
		return (actions.isEmpty() && currentAction.isFinished());
	}

	public void executeStep(SimState state)
	{
		if(currentAction == null)
		{
			initialize();
		}
		
		if(currentAction.isFinished())
		{
			currentAction = actions.poll();
		}
		
		currentAction.executeStep(state);
	}
}
