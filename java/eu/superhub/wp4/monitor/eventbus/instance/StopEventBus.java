package eu.superhub.wp4.monitor.eventbus.instance;

public class StopEventBus {
	public String getProblems() {
		String res;

		if (StartEventBus.ebd == null || !StartEventBus.ebd.isRunning()) {
			res = "EventBus instance not running. Please start before stopping.";
		} else {
			res = null;
		}

		return res;
	}

	public void run() {
		try {
			StartEventBus.ebd.stop();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			StartEventBus.ebd = null;
		}
	}
}
