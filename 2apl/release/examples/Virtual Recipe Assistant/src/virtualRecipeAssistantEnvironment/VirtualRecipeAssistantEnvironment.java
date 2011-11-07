package virtualRecipeAssistantEnvironment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import data.ActionInRecipe;
import data.Ingredient;
import data.Percepts;
import data.Recipe;
import data.Requirement;
import data.Tool;
import data.ToolInRecipe;

import eis.AgentListener;
import eis.EIDefaultImpl;
import eis.exceptions.ActException;
import eis.exceptions.EnvironmentInterfaceException;
import eis.exceptions.ManagementException;
import eis.exceptions.NoEnvironmentException;
import eis.exceptions.PerceiveException;
import eis.iilang.Action;
import eis.iilang.EnvironmentCommand;
import eis.iilang.Function;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import eis.iilang.Percept;

import gui.CommunicationPanel;
import gui.ControlPanel;
import gui.MainFrame;
import gui.VisualPanel;
import io.XMLException;
import io.XMLReader;

public class VirtualRecipeAssistantEnvironment extends EIDefaultImpl
{
	protected static VirtualRecipeAssistantEnvironment instance;
	
	@Override
	protected LinkedList<Percept> getAllPerceptsFromEntity(String arg0)
	throws PerceiveException, NoEnvironmentException 
	{
		// TODO Auto-generated method stub
		return null;
	}	
	
	public static void main(String[] args)
	{
		VirtualRecipeAssistantEnvironment vrae = new VirtualRecipeAssistantEnvironment();
	}
	
	public static void sendPercept(Percept percept)
	{
		try
		{
			instance.notifyAgents(percept, instance.getAgents().toArray(new String[0]));
		}
		catch (EnvironmentInterfaceException eie) { eie.printStackTrace(); }
	}
	
	public static VirtualRecipeAssistantEnvironment getInstance()
	{
		return instance;
	}
	
	public VirtualRecipeAssistantEnvironment()
	{
		super();

		instance = this;
		
		XMLReader xmlReader;
		try
		{
			xmlReader = XMLReader.getInstance();
			xmlReader.read();
			
			//MainFrame mf = MainFrame.getInstance();
			
			//mf.start();
		}
		catch (ParserConfigurationException pce) { pce.printStackTrace(); } 
		catch (SAXException saxe) { saxe.printStackTrace(); }
		catch (IOException ioe) { ioe.printStackTrace(); }
		catch (XMLException xmle) { xmle.printStackTrace(); }
	}
	
	@Override
	public LinkedList<Percept> performAction(String agent, Action action, String...entities)
	throws ActException, NoEnvironmentException
	{
		String actionName = action.getName().toLowerCase();
		LinkedList<Percept> result = new LinkedList<Percept>();

		if (actionName.equals("concat"))
		{
			if (action.getParameters().size() == 0)
				throw new ActException("The action 'concat' expects at least 1 parameter.");
			
			Iterable<Parameter> list = action.getParameters();
			if (action.getParameters().get(0) instanceof ParameterList)
				list = (ParameterList)action.getParameters().get(0);
			
			String s = "";
			for (Parameter parameter : list)
			{
				if (parameter instanceof Identifier)
					s += ((Identifier)parameter).getValue();
				else if (parameter instanceof Numeral)
					s += ((Numeral)parameter).getValue();
				else
					throw new ActException("The action 'concat' only accepts Identifiers and Numerals.");
			}
			
			result.add(new Percept("sentence", new Identifier(s)));
		}
		else if (actionName.equals("rand"))
		{
			if (action.getParameters().size() != 2)
				throw new ActException("The action 'rand' expects 2 parameters.");
			if (!(action.getParameters().get(0) instanceof Numeral) || !(action.getParameters().get(1) instanceof Numeral))
				throw new ActException("The action 'rand' expects 2 Numerals.");
			
			int l = ((Numeral)action.getParameters().get(0)).getValue().intValue();
			int r = ((Numeral)action.getParameters().get(1)).getValue().intValue();

			result.add(new Percept(
				"random",
				new Numeral(l + new Random().nextInt(r - l))));
		}
		else if (actionName.equals("addspeak"))
		{
			if (action.getParameters().size() < 1)
				throw new ActException("The action 'addSpeak' expects a parameter.");
			
			try { CommunicationPanel.getInstance().addSpeak(((Identifier)action.getParameters().get(0)).getValue()); }
			catch (FileNotFoundException fnfe) { fnfe.printStackTrace(); }
		}
		else if (actionName.equals("clearspeak"))
		{
			try { CommunicationPanel.getInstance().resetSpeakCombo(); }
			catch (FileNotFoundException fnfe) { fnfe.printStackTrace(); }
		}
		else if (actionName.equals("getinstructions"))
		{
			String recipe = action.getParameters().get(0).toProlog().toLowerCase();
			for (int i = 0; i < Recipe.getRecipe(recipe).getActions().size(); i ++)
			{
				ActionInRecipe actionInRecipe = Recipe.getRecipe(recipe).getActions().get(i);
				if (actionInRecipe.getIngredient() == null)
				{
					result.add(new Percept(
						"instruction", 
						new Numeral(i),
						new Identifier(actionInRecipe.getRecipe()),
						new Identifier(actionInRecipe.getName()),
						new Identifier(actionInRecipe.getTool().getName()),
						new Identifier(actionInRecipe.getComment())));
				}
				else
				{
					result.add(new Percept(
						"instruction", 
						new Numeral(i), 
						new Identifier(actionInRecipe.getRecipe()),
						new Identifier(actionInRecipe.getName()),
						new Identifier(actionInRecipe.getTool().getName()),
						new Identifier(actionInRecipe.getIngredient().getName()),
						new Numeral(actionInRecipe.getAmount()),
						new Identifier(actionInRecipe.getComment())));
				}
			}
		}
		else if (actionName.equals("getinstructionscount"))
		{
			String recipe = action.getParameters().get(0).toProlog().toLowerCase();
			
			result.add(new Percept(
				"nrofinstructions", 
				new Numeral(Recipe.getRecipe(recipe).getActions().size())));
		}
		else if (actionName.equals("gettools"))
		{	
			for (Tool tool : Tool.getTools())
			{
				result.add(new Percept(
					"tool",
					new Identifier(tool.getName()),
					new Identifier(tool.getTitle()),
					new Numeral(tool.getCapacity())));
			}
		}
		else if (actionName.equals("gettoolfromaction"))
		{
			if (action.getParameters().size() < 1)
				throw new ActException("The action 'getToolFromAction' expects a parameter.");
			
			String param = ((Identifier)action.getParameters().get(0)).getValue();

			result.add(new Percept(
				"tool",
				new Identifier(Tool.getToolByActionName(param).getName())));
		}
		else if (actionName.equals("getingredients"))
		{
			for (Ingredient ingredient : Ingredient.getIngredients())
			{
				result.add(new Percept(
					"ingredient",
					new Identifier(ingredient.getName()),
					new Identifier(ingredient.getTitle()),
					new Numeral(ingredient.getCapacity()),
					new Identifier(ingredient.getUnit())));
			}		
		}
		else if (actionName.equals("getrequirements"))
		{
			if (action.getParameters().size() < 1)
				throw new ActException("The action 'getrequirements' expects a parameter: the name of the recipe.");
			
			String recipe = action.getParameters().get(0).toProlog().toLowerCase();

			for (Requirement requirement : Recipe.getRecipe(recipe).getRequirements())
			{
				result.add(new Percept(
					"requirement", 
					new Identifier("tool( " + requirement.getId() + " )"),
					new Numeral(requirement.getCapacity())));
			}
		}
		else if (actionName.equals("getrecipies"))
		{
			for (Recipe recipe : Recipe.getRecipies())
			{
				result.add(new Percept(
					"recipe",
					new Identifier(recipe.getName()),
					new Identifier(recipe.getTitle())
				));
			}
		}
		else if (actionName.equals("reset"))
		{
			try { MainFrame.getInstance().reset(); }
			catch (FileNotFoundException fnfe) { throw new ActException(fnfe.getMessage()); }			
		}
		
		return result;
	}
	
	@Override
	public void manageEnvironment(EnvironmentCommand environmentCommand)
	throws ManagementException, NoEnvironmentException 
	{	
		int commandType = environmentCommand.getType();
		
		switch (environmentCommand.getType())
		{
			case EnvironmentCommand.START :
				
				try { MainFrame.getInstance().start(); }
				catch (FileNotFoundException fnfe) { throw new ManagementException(fnfe.getMessage()); }	
				
				break;
			case EnvironmentCommand.KILL :
			
				MainFrame.getInstance().stop();			
				break;
			case EnvironmentCommand.PAUSE :
				
				MainFrame.getInstance().pause();
				break;
			case EnvironmentCommand.RESET :	
				try { MainFrame.getInstance().reset(); }
				catch (FileNotFoundException fnfe) { throw new ManagementException(fnfe.getMessage()); }
				
				break;
			case EnvironmentCommand.INIT :

				break;
			case EnvironmentCommand.MISC :

				break;
			default:
				throw new ManagementException("No such command with type '" + environmentCommand.getType() + "'. Please use one of the following types: 1, 2, 3, 4, 5, 6");
		}
	}

	@Override
	public boolean isConnected() 
	{
		return true;
	}

	@Override
	public void release() 
	{
		MainFrame.getInstance().stop();
	}

	@Override
	public String requiredVersion() 
	{
		return "0.2";
	}
}
