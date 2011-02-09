package net.sf.ictalive.eventbus.dashboard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.sun.messaging.jmq.jmsclient.runtime.BrokerInstance;
import com.sun.messaging.jmq.jmsclient.runtime.ClientRuntime;
import com.sun.messaging.jmq.jmsservice.BrokerEventListener;

public class EventBusDaemon implements IComponent
{
	private BrokerInstance			brokerInstance;
	private boolean					running;
	
	public EventBusDaemon(String baseDir) throws ClassNotFoundException, IllegalAccessException, InstantiationException, IOException
	{
		String[] args;
		
		installMQ(baseDir);
		
		args = new String[] { "-imqhome", baseDir, "-varhome", baseDir + File.separator + "var" };
		
		// obtain the ClientRuntime singleton object
		ClientRuntime clientRuntime = ClientRuntime.getRuntime();

		// create the embedded broker instance
		brokerInstance = clientRuntime.createBrokerInstance();

		// convert the specified broker arguments into Properties
		// this is a utility function: it doesn't change the broker
		Properties props = brokerInstance.parseArgs(args);

		// initialise the broker instance 
		// using the specified properties
		// and a BrokerEventListener
		BrokerEventListener listener = new EventBusBrokerEventListener();
		brokerInstance.init(props, listener);
		
		running = false;
	}

	private void installMQ(String baseDir) throws IOException
	{
		ZipFile					zf;
		Enumeration<? extends ZipEntry>	en;
		ZipEntry				ze;
		InputStream				is;
		File					f;
		FileOutputStream		fos;
		byte[]					data;
		int						read;
		
		f = new File(baseDir + File.separator + ".installed");
		if(!f.exists())
		{
			data = new byte[2048];
			
			f = new File(baseDir + File.separator + "install");
			f.mkdirs();
			
			is = getClass().getClassLoader().getResourceAsStream("mq.zip");
			f = File.createTempFile("messagequeue", ".zip");
			fos = new FileOutputStream(f);
			
			read = is.read(data);
			while(read > 0)
			{
				fos.write(data, 0, read);
				read = is.read(data);
			}
			
			fos.close();
			
			zf = new ZipFile(f);
			en = zf.entries();
			while(en.hasMoreElements())
			{
				ze = en.nextElement();
				f = new File(baseDir + File.separator + ze.getName());
				System.out.println("Installing " + baseDir + File.separator + ze.getName());
				if(ze.isDirectory())
				{
					f.mkdirs();
				}
				else
				{
					is = zf.getInputStream(ze);
					f.delete();
					f.createNewFile();
					fos = new FileOutputStream(f);
					
					read = is.read(data);
					while(read > 0)
					{
						fos.write(data, 0, read);
						read = is.read(data);
					}
					
					fos.close();
				}
			}
			
			f = new File(baseDir + File.separator + ".installed");
			f.createNewFile();
		}
	}

	@Override
	public void start()
	{
		// now start the embedded broker
		brokerInstance.start();
		running = true;
	}

	@Override
	public void stop()
	{
		brokerInstance.stop();
		brokerInstance.shutdown();
		running = false;
	}
	
	public static void main(String args[]) throws ClassNotFoundException, IllegalAccessException, InstantiationException, IOException, InterruptedException
	{
		EventBusDaemon	ebd;
		
		ebd = new EventBusDaemon("/tmp/eventbus");
		ebd.start();
		Thread.sleep(500);
		ebd.stop();
	}

	public boolean isRunning()
	{
		return running;
	}
}