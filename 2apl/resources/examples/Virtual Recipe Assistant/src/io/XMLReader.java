package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import data.Action;
import data.ActionInRecipe;
import data.Constants;
import data.Ingredient;
import data.Recipe;
import data.Requirement;
import data.Tool;
import data.ToolInRecipe;

public class XMLReader 
{
	private DocumentBuilderFactory documentBuilderFactory;
	private DocumentBuilder documentBuilder;
	private static XMLReader instance;
	
	private void readTools()
	throws SAXException, IOException, XMLException
	{
		Document document = documentBuilder.parse(FileReader.getInstance().getTools());
		document.getDocumentElement().normalize();
		
		for (int i = 0; i < document.getDocumentElement().getChildNodes().getLength(); i ++)
		{
			Node nodeTool = document.getDocumentElement().getChildNodes().item(i);
			if (nodeTool.getNodeType() != Node.ELEMENT_NODE)
				continue;

			String toolName = nodeTool.getNodeName();
			
			LinkedList<Action> subNodesActions = new LinkedList<Action>();
			
			// GETTING THE PARAMETER TITLE
			String toolTitle = toolName;
			if (nodeTool.getAttributes().getNamedItem("title") != null)
				toolTitle = nodeTool.getAttributes().getNamedItem("title").getNodeValue();	

			// GETTING THE PARAMETER CAPACITY
			int toolCapacity = -1;
			if (nodeTool.getAttributes().getNamedItem("capacity") != null)
			{
				try
				{
					toolCapacity = Integer.parseInt(nodeTool.getAttributes().getNamedItem("capacity").getNodeValue());
					
					// Appearently the parameter capacity was set. Thus this tool is a container tool
					// and has the action "use" which is used to select a container tool:
					subNodesActions.add(new Action("insert", "use"));
				}
				catch (NumberFormatException nfe) { throw new XMLException("Illegal XML-file; the parameter 'capacity' must contain an integer value"); }
			}
			
			// GETTING THE PARAMETER IMAGE
			ImageIcon imageIconTool = null;
			if (nodeTool.getAttributes().getNamedItem("image") != null)
				imageIconTool = new ImageIcon(FileReader.getInstance().getImages() + "/" + nodeTool.getAttributes().getNamedItem("image").getNodeValue());

			// GETTING THE SUBNODES
			NodeList subNodes = nodeTool.getChildNodes();
			Node subNodeAction;
			for (int i2 = 0; (subNodeAction = subNodes.item(i2)) != null && i2 < subNodes.getLength(); i2 ++)
			{
				if (nodeTool.getNodeType() != Node.ELEMENT_NODE)
					continue;
				
				if (subNodeAction.getAttributes() == null)
					continue;
				
				String subActionName = subNodeAction.getNodeName();
				
				String actionTitle = subActionName;
				if (subNodeAction.getAttributes().getNamedItem("title") != null)
					actionTitle = subNodeAction.getAttributes().getNamedItem("title").getNodeValue();
				
				subNodesActions.add(new Action(subActionName, actionTitle));
			}

			new Tool(toolName, toolTitle, toolCapacity, imageIconTool, subNodesActions.toArray(new Action[0]));
		}
	}
	
	private void readIngredients() 
	throws SAXException, IOException, XMLException
	{
		Document document = documentBuilder.parse(FileReader.getInstance().getIngredients());

		for (int i = 0; i < document.getDocumentElement().getChildNodes().getLength(); i ++)
		{
			Node nodeIngredient = document.getDocumentElement().getChildNodes().item(i);
			if (nodeIngredient.getNodeType() != Node.ELEMENT_NODE)
				continue;
			
			String ingredientName = nodeIngredient.getNodeName();
			
			// GETTING THE PARAMETER TITLE
			String ingredientTitle = ingredientName;
			if (nodeIngredient.getAttributes().getNamedItem("title") != null)
				ingredientTitle = nodeIngredient.getAttributes().getNamedItem("title").getNodeValue();	

			// GETTING THE PARAMETER UNIT
			if (nodeIngredient.getAttributes().getNamedItem("unit") == null)
				throw new XMLException("Illegal XML-file; the tag Ingredient requires an attribute 'unit'");
			String ingredientUnit = nodeIngredient.getAttributes().getNamedItem("unit").getNodeValue();
			
			// GETTING THE PARAMETER AMOUNTS
			if (nodeIngredient.getAttributes().getNamedItem("amounts") == null)
				throw new XMLException("Illegal XML-file; the tag Ingredient requires an attribute 'amounts'");
			int ingredientAmounts = 0;
			try
			{
				ingredientAmounts = Integer.parseInt(nodeIngredient.getAttributes().getNamedItem("amounts").getNodeValue());			
			}
			catch (NumberFormatException nfe) { throw new XMLException("Illegal XML-file; the parameter 'amounts' must contain an integer value"); }
			
			// GETTING THE PARAMETER CAPACITY
			if (nodeIngredient.getAttributes().getNamedItem("capacity") == null)
				throw new XMLException("Illegal XML-file; the tag Ingredient requires an attribute 'capacity'");
			double ingredientCapacity = 0;
			try
			{
				ingredientCapacity = Double.parseDouble(nodeIngredient.getAttributes().getNamedItem("capacity").getNodeValue());			
			}
			catch (NumberFormatException nfe) { throw new XMLException("Illegal XML-file; the parameter 'capacity' must contain a floating-point value"); }				
			
			// GETTING THE PARAMETER IMAGE
			ImageIcon imageIconIngredient = null;
			if (nodeIngredient.getAttributes().getNamedItem("image") != null)
				imageIconIngredient = new ImageIcon(FileReader.getInstance().getImages() + "/" + nodeIngredient.getAttributes().getNamedItem("image").getNodeValue());
			
			new Ingredient(ingredientName, ingredientTitle, ingredientUnit, ingredientAmounts, ingredientCapacity, imageIconIngredient);
		}		
	}
	
	private void readRecipies()
	throws SAXException, IOException, XMLException
	{
		for (String file : FileReader.getInstance().getRecipies())
		{// LOOP THROUGH ALL RECIPIES IN THE FOLDER
			Document document = documentBuilder.parse(file);
			
			if (!document.getDocumentElement().getNodeName().equals("recipe"))
				throw new XMLException("No recipe found in '" + file + "'");
			
			String recipeName = "";
			if (document.getDocumentElement().getAttribute("name") == null)
				throw new XMLException("The recipe in '" + file + "' has no attribute 'name' in the root tag.");
			recipeName = document.getDocumentElement().getAttribute("name");
			
			String recipeTitle = recipeName;
			if (document.getDocumentElement().getAttribute("title") != null)
				recipeTitle = document.getDocumentElement().getAttribute("title");
			
			if (document.getDocumentElement().getElementsByTagName("actions").getLength() == 0)
				throw new XMLException("Recipe has no tag 'actions' containing all the actions in '" + file + "'");
			
			ArrayList<ActionInRecipe> actions = new ArrayList<ActionInRecipe>();
			int counter = -1;
			for (int i = 0; i < document.getDocumentElement().getElementsByTagName("actions").item(0).getChildNodes().getLength(); i ++)
			{// LOOP THROUGH ACTIONS IN RECIPE
				Node nodeAction = document.getDocumentElement().getElementsByTagName("actions").item(0).getChildNodes().item(i);
				if (nodeAction.getNodeType() != Node.ELEMENT_NODE)
					continue;
				
				if (!nodeAction.getNodeName().equals("include") && !Action.doesActionExist(nodeAction.getNodeName()))
					throw new XMLException("The action '" + nodeAction.getNodeName() + "' in recipe '" + file + "' was never defined for any tool.");
				
				String comment = "none";
				if (nodeAction.getAttributes().getNamedItem("comment") != null)
					comment = nodeAction.getAttributes().getNamedItem("comment").getNodeValue();
				
				counter ++;
				
				if (nodeAction.getNodeName().equals("include"))
				{
					if (nodeAction.getAttributes().getNamedItem("recipe") == null)
						throw new XMLException("An include in recipe '" + file + "' does not have the parameter 'recipe'.");
					
					String includedRecipe = nodeAction.getAttributes().getNamedItem("recipe").getNodeValue();
					
					//if (Recipe.getRecipe(includedRecipe) == null)
					//	throw new XMLException("The included recipe '" + includedRecipe + "' in file '" + file + "' does not exist.");
						
					actions.add(new ActionInRecipe(recipeName, "include", comment, new ToolInRecipe( includedRecipe, /*null,*/ -1 ), null, -1));
					
					continue;
				}
				
				ToolInRecipe toolInRecipe = null;
				Ingredient ingredient = null;
				int amount = -1;
				for (int i2 = 0; i2 < nodeAction.getChildNodes().getLength(); i2 ++)
				{// LOOP THROUGH SUBNODES OF AN ACTION
					Node subNode = nodeAction.getChildNodes().item(i2);
					if (subNode.getNodeName().equals("tool"))
					{
						// GETTING THE PARAMETER NAME
						String toolName = "anyTool";
						if (subNode.getAttributes().getNamedItem("name") != null)
						{
							toolName = subNode.getAttributes().getNamedItem("name").getNodeValue();
							
							if (!Tool.doesToolExist(toolName))
								throw new XMLException("The tool '" + toolName + "' as specified in '" + file + "' was never defined.");
						}
						// GETTING THE PARAMETER ID
						else if (subNode.getAttributes().getNamedItem("id") != null)
						{
							toolName = "tool( " + subNode.getAttributes().getNamedItem("id").getNodeValue() + " )";
						}
						
						// GETTING THE PARAMETER CAPACITY
						int toolCapacity = -1;
						if (subNode.getAttributes().getNamedItem("capacity") != null)
						{
							try { toolCapacity = Integer.parseInt(subNode.getAttributes().getNamedItem("capacity").getNodeValue()); }
							catch (NumberFormatException nfe) { throw new XMLException("Illegal XML-file; the parameter 'capacity' must contain an integer value"); }
						}
						
						toolInRecipe = new ToolInRecipe(toolName, /*Action.getAction(nodeAction.getNodeName()), */toolCapacity);
					}
					else if (subNode.getNodeName().equals("ingredient"))
					{
						if (subNode.getAttributes().getNamedItem("name") == null)
							throw new XMLException("The ingredient in '" + file + "' does not have an attribute 'name'.");

						if (subNode.getAttributes().getNamedItem("amount") == null)
							throw new XMLException("The ingredient in '" + file + "' does not have an attribute 'amount'.");
						
						if (!Ingredient.doesIngredientExist(subNode.getAttributes().getNamedItem("name").getNodeValue()))
							throw new XMLException("The ingredient '" + subNode.getAttributes().getNamedItem("name").getNodeValue() + "' was never defined.");
					
						ingredient = Ingredient.getIngredient(subNode.getAttributes().getNamedItem("name").getNodeValue());

						try { amount = Integer.parseInt(subNode.getAttributes().getNamedItem("amount").getNodeValue()); }
						catch (NumberFormatException nfe) { throw new XMLException("Illegal XML-file; the parameter 'amount' must contain an integer value"); } 
					}
				}

				actions.add(new ActionInRecipe(recipeName, nodeAction.getNodeName(), comment, toolInRecipe, ingredient, amount));
			}
			
			ArrayList<Requirement> requirements = new ArrayList<Requirement>();
			if (document.getDocumentElement().getElementsByTagName("requirements").getLength() > 0)
			{
				for (int i = 0; i < document.getDocumentElement().getElementsByTagName("requirements").item(0).getChildNodes().getLength(); i ++)
				{// LOOP THROUGH REQUIREMENTS IN RECIPE			
					Node nodeRequirement = document.getDocumentElement().getElementsByTagName("requirements").item(0).getChildNodes().item(i);
				
					if (!nodeRequirement.getNodeName().equals("tool"))
						continue;
					
					int id = -1;
					int capacity = -1;
					
					// GETTING THE PARAMETER ID
					if (nodeRequirement.getAttributes().getNamedItem("id") == null)
						throw new XMLException("A requirement must have a parameter 'id' with an integer value");
					
					try { id = Integer.parseInt(nodeRequirement.getAttributes().getNamedItem("id").getNodeValue()); }
					catch (NumberFormatException nfe) { throw new XMLException("Illegal XML-file; the parameter 'capacity' must contain an integer value"); }
				
					// GETTING THE PARAMETER CAPACITY
					if (nodeRequirement.getAttributes().getNamedItem("capacity") == null)
						throw new XMLException("A requirement must have a parameter 'capacity' with an integer value");
					
					try { capacity = Integer.parseInt(nodeRequirement.getAttributes().getNamedItem("capacity").getNodeValue()); }
					catch (NumberFormatException nfe) { throw new XMLException("Illegal XML-file; the parameter 'capacity' must contain an integer value"); }
					
					requirements.add(new Requirement(id, capacity));
				}
			}

			Recipe recipe = new Recipe(recipeName, recipeTitle, requirements.toArray(new Requirement[0]), actions);
		}
	}
	
	protected XMLReader()
	throws ParserConfigurationException
	{
		documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilder = documentBuilderFactory.newDocumentBuilder();
	}
	
	public static XMLReader getInstance() 
	throws ParserConfigurationException
	{
		if (instance == null)
			instance = new XMLReader();
		
		return instance;
	}
	
	public void read() 
	throws SAXException, IOException, XMLException
	{
		readTools();
		readIngredients();
		readRecipies();
		
		for (Recipe recipe : Recipe.getRecipies())
			recipe.parse();
	}
}
