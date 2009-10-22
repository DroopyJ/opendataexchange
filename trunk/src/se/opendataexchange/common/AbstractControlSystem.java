package se.opendataexchange.common;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import se.opendataexchange.common.polling.IPollData;
import se.opendataexchange.controller.OpenDataExchangeController;

public abstract class AbstractControlSystem implements IControlSystem {
	
	private static Logger l = Logger.getLogger(AbstractControlSystem.class);
	private ArrayList<AddressSpace> lstAddressSpaces = new ArrayList<AddressSpace>();
	private ArrayList<IDataUpdate> lstDataObservers = new ArrayList<IDataUpdate>();
	private ArrayList<IControlSystemEventUpdate> lstControlSystemObservers = new ArrayList<IControlSystemEventUpdate>();
	private IPollData dataSource;
	private OpenDataExchangeController controller;
	private static HashMap<String, Long> errorLog = new HashMap<String, Long>();
		
	public AbstractControlSystem() {
		l = Logger.getLogger(this.getClass());
	}

	
	@Override
	public void setController(OpenDataExchangeController controller) {
		this.controller = controller;
		
	}

	public OpenDataExchangeController getController(){
		return this.controller;
	}

	public IPollData getDataSource() {
		return dataSource;
	}


	public void setDataSource(IPollData dataSource) {
		this.dataSource = dataSource;
	}

	public ArrayList<AddressSpace> getLstAddressSpaces() {
		return lstAddressSpaces;
	}

	public void setLstAddressSpaces(
			ArrayList<AddressSpace> lstAddressSpaces) {
		this.lstAddressSpaces = lstAddressSpaces;
	}

	public void init(){
		for(AddressSpace space : lstAddressSpaces){
			controller.addAddressSpace(space);
		}
	}

	@Override
	public void addControlSystemEventObserver(IControlSystemEventUpdate observer) {
		synchronized (lstControlSystemObservers) {
			lstControlSystemObservers.add(observer);
		}

	}

	@Override
	public void addDataValueObserver(String[] names, IDataUpdate observer) {
		synchronized (lstDataObservers) {
			lstDataObservers.add(observer);
		}

	}

	@Override
	public Object getDataValue(String name) {
		if(dataSource!=null)
			return dataSource.readData(name);
		return null;
	}

	@Override
	public String[] getDataValueObservers(String names) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getDataValues(String[] names) {
		if(dataSource!=null)
			return dataSource.readData(names);
		return null;
	}

	@Override
	public int getNumberOfObservers() {
		synchronized (lstControlSystemObservers) {
			return lstControlSystemObservers.size();
		}
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeControlSystemEventObserver(IControlSystemEventUpdate observer) {
		synchronized (lstControlSystemObservers) {
			lstControlSystemObservers.remove(observer);
		}
	}

	@Override
	public void removeDataValueObserver(String[] names, IDataUpdate observer) {
		synchronized (lstDataObservers) {
			lstDataObservers.remove(observer);
		}
	}

	@Override
	public void setDataValue(String name, Object value) {
		if(dataSource!=null){
			dataSource.writeData(name, value);
		}
	}

	@Override
	public void setDataValues(String[] names, Object[] values) {
		if(names.length!=values.length)return;
		for(int x=0;x<names.length;x++){
			setDataValue(names[x], values[x]);
		}
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}
	/***
	 * Report an error to log book. Report same error at most once a minute.
	 * @param e
	 */
	public static void reportError(ErrorInfo e){
		if (!errorLog.containsKey(e.getMessage()))
			l.fatal(e.getMessage());
		else if (System.currentTimeMillis() - errorLog.get(e.getMessage()) > 60000)
			l.fatal(e.getMessage());
		errorLog.put(e.getMessage(), System.currentTimeMillis());
	}
	
	public static void reportDebug(String msg){
		l.debug(msg);
	}
}
