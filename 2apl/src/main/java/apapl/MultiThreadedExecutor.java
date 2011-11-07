package apapl;

import java.util.*;

/**
 * A specific executor that adopts the strategy of executing every module in its
 * own thread. To avoid zombie threads, an executor should always be stopped
 * before deinstantiating.
 */
public class MultiThreadedExecutor implements Executor
{
	/** List of modules and their executors. (corresponding to m thread) */
	private HashMap<APLModule, ModuleExecutor> execs;

	/**
	 * List of listeners that are informed whenever a module is started,
	 * stopped, put to sleep or waken up.
	 */
	private ArrayList<MASExecutionListener> listeners;

	/**
	 * Constructs an executor.
	 */
	public MultiThreadedExecutor()
	{
		execs = new HashMap<APLModule, ModuleExecutor>();
		listeners = new ArrayList<MASExecutionListener>();
	}

	/**
	 * Constructs an executor using initial set of executed modules.
	 * 
	 * @param modules the modules to execute
	 */
	public MultiThreadedExecutor(Collection<APLModule> modules)
	{
		execs = new HashMap<APLModule, ModuleExecutor>();
		listeners = new ArrayList<MASExecutionListener>();

		for (APLModule module : modules)
		{
			addModule(module);
		}
	}

	public synchronized void addModule(APLModule module)
	{
		execs.put(module, new ModuleExecutor(module, listeners));
	}

	public synchronized void removeModule(APLModule module)
	{
		scheduleStop(module);
		execs.remove(module);
	}

	public Set<APLModule> getModules()
	{
		return new HashSet<APLModule>(execs.keySet());
	}

	public void addMASExecutionListener(MASExecutionListener listener)
	{
		listeners.add(listener);

		// Creating local copy of the list of executors to allow thread safe
		// access
		Vector<ModuleExecutor> executors = null;
		synchronized(this) {
			executors = new Vector<ModuleExecutor>(execs.values());
		}
		
		// Propagate the change to all executors
		for (ModuleExecutor exec : executors)
		{
			exec.setListeners(listeners);
		}
	}

	public void start()
	{
		// Creating local copy of the list of executors to allow thread safe
		// access
		Vector<ModuleExecutor> executors = null;
		synchronized(this) {
			executors = new Vector<ModuleExecutor>(execs.values());
		}

		for (ModuleExecutor exec : executors)
		{
			// Creates a separate thread and runs the deliberation cycle of the
			// module in it
			exec.execute();
		}
	}

	public void step(int n)
	{
		// Creating local copy of the list of executors to allow thread safe
		// access
		Vector<ModuleExecutor> executors = null;
		synchronized(this) {
			executors = new Vector<ModuleExecutor>(execs.values());
		}

		for (ModuleExecutor exec : executors)
		{
			// Creates a separate thread and runs n steps in it
			exec.execute(n);
		}
	}

	public void stop()
	{		
		// Creating local copy of the list of executors to allow thread safe
		// access
		Vector<ModuleExecutor> executors = null;
		synchronized(this) {
			executors = new Vector<ModuleExecutor>(execs.values());
		}
		
		for (ModuleExecutor exec : executors)
		{
			exec.finish();
		}
	}

	public void start(APLModule module)
	{
		ModuleExecutor exec = null;
		synchronized(this) {
			exec = execs.get(module);
		}
		exec.execute();
	}

	public void step(APLModule module, int n)
	{
		ModuleExecutor exec = null;
		synchronized(this) {
			exec = execs.get(module);
		}
		exec.execute(n);
	}

	public void stop(APLModule module)
	{
		ModuleExecutor exec = null;
		synchronized(this) {
			exec = execs.get(module);
		}
		exec.finish();
	}
	/**
	 * Makes module to go to sleeping mode.
	 * <p>
	 * This method should never be called from a synchronized block or method as
	 * it invokes module execution callbacks.
	 * 
	 * @param module the module to be put to sleep
	 */
	public void sleep(APLModule module)
	{
		ModuleExecutor exec = null;
		synchronized(this) {
			exec = execs.get(module);
		}
		exec.sleep();
	}

	public void wakeUp(APLModule module)
	{
		ModuleExecutor exec = null;
		synchronized(this) {
			exec = execs.get(module);
		}
		exec.wakeUp();
	}

	/**
	 * Schedules module execution to stop after the current step.
	 * 
	 * @param module the module to stop
	 */
	private synchronized void scheduleStop(APLModule module)
	{
		ModuleExecutor exec = null;
		synchronized(this) {
			exec = execs.get(module);
		}
		exec.finish();
	}

	public synchronized void passControl(APLModule srcModule,
			APLModule dstModule)
	{
		// Determine whether source module was executed in run mode or in
		// stepping mode
		int srcStepsToGo = execs.get(srcModule).getStepsToGo();
		removeModule(srcModule);
		addModule(dstModule);

		if (srcStepsToGo == -1)
		{
			// srcModule was executed in the run mode
			start(dstModule);
		}
	}

	/**
	 * The single module executor. Each module is executed in its own thread.
	 * <p>
	 * The lifecycle of the execution thread is controlled by the
	 * <code>stepsToGo</code> counter, which is in case of stepping mode
	 * decreased after each executed deliberation step. To force continuous
	 * execution the <code>stepsToGo</code> counter needs to be set to
	 * <code>-1</code>. In order to make the module execution thread stop as
	 * soon as the current deliberation step is finished the counter needs to be
	 * explicitly set to <code>0</code>.
	 */
	private class ModuleExecutor implements Runnable
	{
		/** The thread used for the execution */
		private Thread t;
		/** The module to be executed */
		private APLModule m;
		/**
		 * The number of remaining deliberation steps to be executed. If set to
		 * <tt>-1</tt>, module will run until explicitly stopped.
		 */
		private int stepsToGo;
		
		/** The list of listeners */
		private ArrayList<MASExecutionListener> listeners;

		/**
		 * Constructs a module executor.
		 * 
		 * @param m the module to be executed
		 * @param listeners the listeners to be informed about the current
		 *        execution state
		 */
		public ModuleExecutor(APLModule m,
				ArrayList<MASExecutionListener> listeners)
		{
			this.m = m;
			this.listeners = new ArrayList<MASExecutionListener>(listeners);
		}

		/**
		 * Executes n deliberation steps in a separate thread.
		 * 
		 * @param n number of steps to be executed
		 */
		public synchronized void execute(int n)
		{
			if (t == null || !t.isAlive())
			{
				t = new Thread(null, this, "ModuleExecutor-" + m.getName());
				// Stop after n deliberation steps
				setStepsToGo(n);
				t.start();
			}
		}

		/**
		 * Executes module in a separate thread until explicitly stopped.
		 */
		public synchronized void execute()
		{
			execute(-1);
		}

		/**
		 * Stops the module execution thread.
		 * 
		 */
		public synchronized void finish()
		{
			if (t != null && t.isAlive())
			{
				setStepsToGo(0);
				wakeUp();
			}
		}

		/**
		 * Suspends the execution of the module execution thread. If the module
		 * is in stepping mode, the method has no effect on the execution.
		 * <p>
		 * This method should be never called from a <code>synchronized</code>
		 * block or method that holds monitor on module as it invokes 
		 * module execution callbacks that may need to acquire module
		 * monitor as well.
		 */
		public void sleep()
		{
			// Do not go to sleep if the module is executed in the stepping mode
			if (getStepsToGo() != (-1))
				return;

			// Make sure that the sleep function is called only from the
			// module's execution thread
			if (t != null && t.isAlive() && t == Thread.currentThread())
			{
				notifyModulePutToSleepListeners();
				
				synchronized(this)
				{					
					// Sleep until new message or external event arrives or
					// until the module executor has been explicitly stopped.
					while ( m.getMessageCount() == 0
							&& m.getEEventCount() == 0
							&& getStepsToGo() != 0 )
					{
						/* 
						 * Sleeps the thread and releases lock on the
						 * module executor. The condition has to be continuously 
						 * re-checked to avoid race condition problem when 
						 * message arrives after the condition is evaluated, 
						 * but before the thread is sent to sleep. 
						 */						
						try
						{
							wait(1000);
						} catch (InterruptedException e)
						{
							// Waken up ...
						}						
					}
				}					

				notifyModuleWillWakeUpListeners();

			} else
			{
				throw new RuntimeException(
						"MultiThreadedExecutor.ModuleExecutor.sleep() called " +
						"from worng thread.");
			}

		}

		/**
		 * Resumes execution of the previously suspended thread.
		 */
		public synchronized void wakeUp()
		{			
			if (t != null && t.isAlive())
			{				
				// Wakes up the execution thread.			
				notifyAll();				
			}			
		}

		/**
		 * The run method of the execution thread. It implements the actual
		 * execution logic of the deliberation cycle.
		 */
		public void run()
		{
			notifyModuleWillStartListeners();

			do
			{
				decStepsToGo();
				m.step();
			} while (getStepsToGo() != 0);

			notifyModuleStoppedListeners();
		}

		/**
		 * Returns the number of remaining deliberation steps. That is, the
		 * number of steps that still have to be executed within this thread.
		 */
		public synchronized int getStepsToGo()
		{
			return stepsToGo;
		}

		/**
		 * Decreases the steps-to-go counter.
		 */
		public synchronized void decStepsToGo()
		{
			if (stepsToGo > 0)
				stepsToGo--;
		}
		
		/**
		 * Sets the value of the steps-to-go counter.
		 *
		 * @param n the new value to set
		 */
		private synchronized void setStepsToGo(int n)
		{
			stepsToGo = n;
		}
		
		private void notifyModuleWillStartListeners()
		{
			// Obtain the copy of listeners as the they are typically updated
			// within another thread
			ArrayList<MASExecutionListener> lc;
			synchronized (this)
			{
				lc = new ArrayList<MASExecutionListener>(listeners);
			}

			for (MASExecutionListener mel : lc)
			{
				mel.moduleWillStart(m);
			}
		}

		private void notifyModuleWillWakeUpListeners()
		{
			// Obtain the copy of listeners as the they are typically updated
			// within another thread
			ArrayList<MASExecutionListener> lc;
			synchronized (this)
			{
				lc = new ArrayList<MASExecutionListener>(listeners);
			}

			for (MASExecutionListener mel : lc)
			{
				mel.moduleWillWakeUp(m);
			}
		}

		private void notifyModuleStoppedListeners()
		{
			// Obtain the copy of listeners as the they are typically updated
			// within another thread
			ArrayList<MASExecutionListener> lc;
			synchronized (this)
			{
				lc = new ArrayList<MASExecutionListener>(listeners);
			}

			for (MASExecutionListener mel : lc)
			{
				mel.moduleStopped(m);
			}
		}

		private void notifyModulePutToSleepListeners()
		{
			// Obtain the copy of listeners as the they are typically updated
			// within another thread
			ArrayList<MASExecutionListener> lc;
			synchronized (this)
			{
				lc = new ArrayList<MASExecutionListener>(listeners);
			}

			for (MASExecutionListener mel : lc)
			{
				mel.modulePutToSleep(m);
			}
		}

		/**
		 * Sets the list of module execution listeners. Each module executor
		 * maintains its own copy of the list.
		 * 
		 * @param listeners the list of listeners to set
		 */
		public synchronized void setListeners(
				ArrayList<MASExecutionListener> listeners)
		{
			this.listeners = new ArrayList<MASExecutionListener>(listeners);
		}
	}

}
