package se.opendataexchange.common;

import se.opendataexchange.controller.OpenDataExchangeController;

public interface IControlSystem {

	void setController(OpenDataExchangeController controller);
	void init();
	void start();
	void stop();
	void pause();
	
	Object[] getDataValues(String[] names);
	void setDataValues(String[] names, Object[] values);
	Object getDataValue(String name);
	void setDataValue(String name, Object value);
	
	void addDataValueObserver(String[] names,IDataUpdate observer);
	void removeDataValueObserver(String[] names, IDataUpdate observer);
	String[] getDataValueObservers(String names);
	
	void addControlSystemEventObserver(IControlSystemEventUpdate observer);
	void removeControlSystemEventObserver(IControlSystemEventUpdate observer);
	int getNumberOfObservers();
}
