package se.opendataexchange.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import se.opendataexchange.common.AddressSpace;
import se.opendataexchange.common.AddressUnit;
import se.opendataexchange.common.AddressValue;
import se.opendataexchange.common.ErrorInfo;
import se.opendataexchange.common.IAddressUnitChanged;
import se.opendataexchange.common.IAddressValueChanged;
import se.opendataexchange.common.IControlSystem;
import se.opendataexchange.common.IControlSystemEventUpdate;
import se.opendataexchange.common.InvalidAddressException;

public class OpenDataExchangeController implements IControlSystemEventUpdate {
	
	private static Logger logger = Logger.getLogger(OpenDataExchangeController.class);

	private static final String defaultPath = "odebeans.xml";
	/*
	 * Static part of this class
	 */
	/*
	 * Method to get the singleton.
	 * @return the only OpenDataExchangeController
	 */
	public static OpenDataExchangeController getController(String path){
		if(path==null || path.equals("")) path = defaultPath;
		synchronized (classLock) {
			if(controller==null){
				ApplicationContext context = new ClassPathXmlApplicationContext(path);
				controller = (OpenDataExchangeController)context.getBean("OpenDataExchangeContext");
				controller.init();
			}
			return controller;
		}
	}
	/*
	 * Static member holding the singleton.
	 */
	private static OpenDataExchangeController controller;
	/*
	 * Static member holding the synchronizer for the singleton calls.
	 */
	private static Object classLock = OpenDataExchangeController.class;

	/*
	 * Member holding the different address spaces.
	 */
	private HashMap<String, AddressSpace> mapSpaces = new HashMap<String, AddressSpace>();
	
	/***
	 * Member holding the control systems defined for this controller via Spring configuration.
	 */
	private ArrayList<IControlSystem> lstControlSystems = new ArrayList<IControlSystem>();

	/*
	 * Default constructor
	 */
	public OpenDataExchangeController(){
	}
	/*
	 * Method that will initialize the singleton. Can be repeated.
	 */
	public void init(){
		//Read the spring configuration given.
		for(IControlSystem system:lstControlSystems){
			system.setController(this);
			system.init();
			system.start();
		}
	}

	public ArrayList<IControlSystem> getLstControlSystems() {
		return lstControlSystems;
	}
	
	public void setLstControlSystems(ArrayList<IControlSystem> lstControlSystems) {
		this.lstControlSystems = lstControlSystems;
	}
	
	public Collection<AddressSpace> getAddressSpaces(){
		synchronized (mapSpaces) {
			return mapSpaces.values();
		}
	}

	public String[] getAddressValues(){
		ArrayList<String> res = new ArrayList<String>();
		for(AddressSpace space : mapSpaces.values()){
			if(!space.getName().equals("ODE"))
				res.addAll(space.getAddresses());
		}
		String[] result = res.toArray(new String[0]);
		return result;
	}
	
	public Object getValue(String addressName) throws InvalidAddressException{
		AddressSpace space = mapSpaces.get((addressName.split("/"))[0]); 
		if(space != null){
			return space.getAddressValue(addressName).getValue();
		}
		throw new InvalidAddressException("Couldn't find the address "+addressName);
	}
	
	public HashMap<String, Object> getAllValues(){
		HashMap<String, Object> map = new HashMap<String, Object>();
		for(AddressSpace space : mapSpaces.values()){
			if(!space.getName().equals("ODE"))
				map.putAll(space.getAllValues());
		}
		return map;
	}
	
	@Override
	public void newEvent(String[] names, Object[] values) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void addAddressSpace(AddressSpace space) {
		synchronized (mapSpaces) {
			mapSpaces.put(space.getName(),space);
		}

	}

	@Override
	public void addToAddressSpace(String address, AddressUnit unitToAdd) throws InvalidAddressException {
		synchronized (mapSpaces) {
			
			AddressSpace space = mapSpaces.get(address);
			if(space == null) throw new InvalidAddressException("Address space with name, \""+address+"\", doesn't exist in this application");
			space.addAddressUnit(unitToAdd.getName(), unitToAdd);
		}

	}

	@Override
	public void addToAddressUnit(String address, AddressValue valueToAdd) throws InvalidAddressException{
		synchronized (mapSpaces) {
			for(AddressSpace space:mapSpaces.values()){
				if(address.contains(space.getName())){
					space.getAddressUnit(address).addAddressValue(valueToAdd.getName(),valueToAdd);
				}
			}
		}

	}

	@Override
	public void removeFromAddressSpace(String address) throws InvalidAddressException{
		// TODO Auto-generated method stub

	}
	
	public void pause(){
		for(IControlSystem system:lstControlSystems){
			system.pause();
		}
	}
	
	public void stop(){
		for(IControlSystem system:lstControlSystems){
			system.stop();
		}
	}
	
	public void start(){
		for(IControlSystem system:lstControlSystems){
			system.start();
		}
	}

	public static AddressValue getErrorValue(){
		if(controller != null){
			for(AddressSpace space : controller.getAddressSpaces()){
				if(space.getName().equals("ODE")){
					try {
						return space.getAddressValue("Ode/Error");					
					} catch (InvalidAddressException e) {
						logger.error(e);
					}				
				}
			}
		}
		return null;
	}
	
	public static void reportError(ErrorInfo error){
		AddressValue v = getErrorValue();
		if(v!=null) v.newData(error, new Date());
	}
	
	public void subscribeForAddressValue(String addressName, IAddressValueChanged observer) throws InvalidAddressException{
		AddressSpace space = mapSpaces.get((addressName.split("/"))[0]); 
		if(space != null){
			space.subscribeForAddressValue(addressName, observer);
			return;
		}
		throw new InvalidAddressException("Couldn't find the address "+addressName);
	}
	
	public void unsubscribeForAddressValue(String addressName, IAddressValueChanged observer) throws InvalidAddressException{
		AddressSpace space = mapSpaces.get((addressName.split("/"))[0]); 
		if(space != null){
			space.unsubscribeForAddressValue(addressName, observer);
			return;
		}
		throw new InvalidAddressException("Couldn't find the address "+addressName);
	}
	
	public void subscribeForAddressUnit(String addressName, IAddressUnitChanged observer) throws InvalidAddressException{
		AddressSpace space = mapSpaces.get((addressName.split("/"))[0]); 
		if(space != null){
			space.subscribeForAddressUnit(addressName, observer);
			return;
		}
		throw new InvalidAddressException("Couldn't find the address "+addressName);
	}

	public void unsubscribeForAddressUnit(String addressName, IAddressUnitChanged observer) throws InvalidAddressException{
		AddressSpace space = mapSpaces.get((addressName.split("/"))[0]); 
		if(space != null){
			space.unsubscribeForAddressUnit(addressName, observer);
			return;
		}
		throw new InvalidAddressException("Couldn't find the address "+addressName);
	}
	
	
	public boolean updateAddressValue(String addressName, Object value){
		try {
			AddressSpace space = mapSpaces.get((addressName.split("/"))[0]);
			if(space != null){
				AddressValue val = space.getAddressValue(addressName);
				val.newData(value, new Date());
				return true;
			}
		} catch (InvalidAddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	public static void destroyController() {
		synchronized (classLock) {
			controller = null;
		}
	}
}
