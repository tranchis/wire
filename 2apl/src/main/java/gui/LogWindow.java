package gui;

import java.util.LinkedList;

import javax.swing.JList;

import apapl.deliberation.DeliberationResult;

public class LogWindow extends JList
{
	// maximum number of items to show
	private int max = 100;

	// one item corresponds to a piece of text (one or more lines)
	private LinkedList<String> items;

	public LogWindow()
	{
		items = new LinkedList<String>();
		setCellRenderer(new LogListCellRenderer());
	}

	/**
	 * Adds information from the {@link DeliberationResult} to the log. Special
	 * tokens "[STEP", "[Significant]" and "[Significant]" are later processed
	 * by {@link LogListCellRenderer}, which controls the way how are the log
	 * rows formatted.
	 * 
	 * @param result the deliberation result to add to the log
	 */
	public void showDeliberationResult(DeliberationResult result)
	{
		items.add("[STEP: " + result.stepName() + "]");
		for (DeliberationResult.InfoMessage m : result.listInfo())
		{
			if (m.getSeverity() == DeliberationResult.InfoMessage.Severity.SIGNIFICANT)
				items.add("[Significant]" + m.getMessage());
			else if (m.getSeverity() == DeliberationResult.InfoMessage.Severity.ERROR)
				items.add("[Error]" + m.getMessage());
			else
				items.add(m.getMessage());
		}

		while (items.size() > max)
		{
			items.removeFirst();
		}

		setListData(items.toArray());
		this.ensureIndexIsVisible(items.size() - 1);
	}
	
	public void clear() {
		items = new LinkedList<String>();
		setListData(items.toArray());
	}
}
