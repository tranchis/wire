package eu.superhub.wp4.monitor.commons;

public class Singleton 
{
	private static int monitor_id = 0;
	
	public static int get_monitor_id()
	{
		monitor_id++;
		return monitor_id;
	}
}
