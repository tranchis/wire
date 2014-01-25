package eu.superhub.wp4.monitor.wp3servicedata;

import eu.superhub.wp3.models.situationaldatamodel.statements.Statement;

public interface ISituationalDataListener {

	void push(Statement s);

}
