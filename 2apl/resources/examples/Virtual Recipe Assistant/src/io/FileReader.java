package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import data.Constants;

public class FileReader 
{
	protected static FileReader instance;
	
	protected FileReader()
	{
		
	}
	
	public static FileReader getInstance()
	{
		if (instance == null)
			instance = new FileReader();
		
		return instance;
	}
	public String getHome() 
	throws FileNotFoundException
	{
		try
		{
			//FIXME TIJDELIJKE OPLOSSING
			try
			{
				return (FileReader.class.getResource("").getPath().split("[\\w]+\\.jar\\!")[0].split("file:/")[1]).replaceAll("%20", " ");
			}
			catch (Exception e) { return ""; }
		}
		catch (ArrayIndexOutOfBoundsException aioobe) { throw new FileNotFoundException("Please rename the jar-file to '" + Constants.JAR + "'"); }
	}
	
	public String getActions() 
	throws FileNotFoundException
	{
		if (!new File(getHome() + "xml/actions.xml").exists())
			throw new FileNotFoundException("Could not load actions in '" + getHome() + "xml/actions.xml'");
		
		return getHome() + "xml/actions.xml";
	}
	
	public String getTools() 
	throws FileNotFoundException
	{
		if (!new File(getHome() + "xml/tools.xml").exists())
			throw new FileNotFoundException("Could not load tools in '" + getHome() + "xml/tools.xml'");
		
		return getHome() + "xml/tools.xml";
	}
	
	public String getIngredients() 
	throws FileNotFoundException
	{
		if (!new File(getHome() + "xml/ingredients.xml").exists())
			throw new FileNotFoundException("Could not load ingredients in '" + getHome() + "xml/ingredients.xml'");
		
		return getHome() + "xml/ingredients.xml";
	}
	
	public String[] getRecipies() 
	throws FileNotFoundException
	{
		if (!new File(getHome() + "xml/recipies").exists())
			throw new FileNotFoundException("Was expecting folder 'recipies' in '" + getHome() + "xml'");
		
		ArrayList<String> result = new ArrayList<String>();
		for (File file : new File(getHome() + "xml/recipies").listFiles())
			if (file.getAbsolutePath().toLowerCase().endsWith(".xml"))
				result.add(file.getAbsolutePath());

		if (result.size() == 0)
			throw new FileNotFoundException("No xml files found in '" + getHome() + "xml'");
		
		return result.toArray(new String[0]);
	}
	
	public String getImages() 
	throws FileNotFoundException
	{
		return getHome() + "img";
	}
}
