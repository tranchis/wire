package apapl;

public class LogEntry {
	
	public long nanoTime = System.nanoTime();
	public long currentTimeMillis = System.currentTimeMillis();
	
	public String data;
	public String comment;

	public LogEntry(String data, String comment) {

		this.data = data;
		this.comment = comment;
	
	}
	
	public String toString() {
		
		String ret = "";
		
		ret += data + "(" + comment + ")";
		
		return ret;
		
	}
	
}