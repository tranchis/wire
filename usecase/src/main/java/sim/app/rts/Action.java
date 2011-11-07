package sim.app.rts;

import sim.engine.SimState;

public interface Action
{

	boolean isFinished();

	void executeStep(final SimState state);

}
