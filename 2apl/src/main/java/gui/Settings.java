package gui;

public class Settings
{
	public static int OVERVIEW = 1;
	public static int LOG = 2;
	public static int BELIEFUPDATES = 3 ;
	public static int PCRULES = 4;
	public static int PRRULES = 5;
	public static int PGRULES = 6;
	public static int FILES = 7;
	public static int STATETRACER = 8;
	public static int FGDC = 9;
	public static int MESSAGES = 10;
	
	private static int selectedTab = 0;
	//private static boolean useSH = false;
	private static boolean showFGDC = false;
	private static boolean showTracer = false;
	//private static boolean forAll = true;
	
	public static void setSelectedTab(int i)
	{
		selectedTab = i;
	}
	
	public static int getSelectedTab()
	{
		return selectedTab;
	}
	
	public static boolean showFGDC()
	{
		return showFGDC;
	}
	
	public static void showFGDC(boolean b)
	{
		showFGDC = b;
	}
	
	public static boolean showTracer()
	{
		return showTracer;
	}
	
	public static void showTracer(boolean b)
	{
		showTracer = b;
	}
	
	/*public static boolean forAll()
	{
		return forAll;
	}
	
	public static void forAll(boolean b)
	{
		forAll = b;
	}*/
	

}
