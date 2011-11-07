package apapl;

import java.lang.reflect.*;
import java.io.*;
import java.net.*;

/**
 * Used to add a jar file to the classpath. This is third party code. Used to 
 * add environment archives to the application.
 */
public class ClassPathHacker 
{
	
private static final Class[] parameters = new Class[]{URL.class};

/**
 * Adds a file to the classpath.
 * @param s a String pointing to the file
 * @throws IOException
 */
public static void addFile(String s) throws IOException 
{
	File f = new File(s);
	addFile(f);
}

/**
 * Adds a file to the classpath
 * @param f the file to be added
 * @throws IOException
 */
public static void addFile(File f) throws IOException 
{
	addURL(f.toURL());
}
 
/**
 * Adds the content pointed by the URL to the classpath.
 * @param u the URL pointing to the content to be added
 * @throws IOException
 */
public static void addURL(URL u) throws IOException {
		
	URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
	Class sysclass = URLClassLoader.class;
 
	try {
		Method method = sysclass.getDeclaredMethod("addURL",parameters);
		method.setAccessible(true);
		method.invoke(sysloader,new Object[]{ u });
	} catch (Throwable t) {
		t.printStackTrace();
		throw new IOException("Error, could not add URL to system classloader");
	}
		
}
 
}