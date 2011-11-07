package com.github.tranchis.wire.test.scenario.mason;

import sim.engine.SimState;

public interface Action
{

	boolean isFinished();

	void executeStep(final SimState state);

}
