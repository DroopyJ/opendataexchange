package se.opendataexchange.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

public abstract class AddressUnit implements IAddressUnitChanged, IAddressValueChanged{

	private HashMap<String,AddressUnit> mapUnits = new HashMap<String, AddressUnit>();
	private HashMap<String,AddressValue> mapValues = new HashMap<String, AddressValue>();
	private Vector<AddressValue> changedValues = new Vector<AddressValue>(); 
	private Vector<AddressUnit> changedUnits = new Vector<AddressUnit>();
	private ArrayList<IAddressUnitChanged> observers = new ArrayList<IAddressUnitChanged>();
	private String name;
	
	
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public HashMap<String, AddressUnit> getMapUnits() {
		return mapUnits;
	}
	
	public void setMapUnits(HashMap<String, AddressUnit> mapUnits) {
		this.mapUnits = mapUnits;
	}
	
	public HashMap<String, AddressValue> getMapValues() {
		return mapValues;
	}
	
	public void setMapValues(HashMap<String, AddressValue> mapValues) {
		this.mapValues = mapValues;
	}
	
	public void addAddressUnit(String name, AddressUnit unit){
		synchronized (mapUnits) {
			mapUnits.put(name, unit);
		}
	}
	public void addAddressUnits(String[] names, AddressUnit[] units){
		if(name.length()!= units.length)return;
		synchronized (mapUnits) {
			for(int x =0;x<name.length();x++){
				mapUnits.put(names[x], units[x]);
			}
		}
	}
	
	public void removeAddressUnit(String name){
		synchronized (mapUnits) {
			mapUnits.remove(name);
		}
	}

	/***
	 * Method that traverses the full name space after the unit name or throws an Invalid Address. 
	 * @param name
	 * @return AddressUnit with the name.
	 */
	public AddressUnit getAddressUnit(String name) throws InvalidAddressException{
		if(mapUnits.containsKey(name)){
			synchronized (mapUnits) {
				return mapUnits.get(name);
			}
		}else{
			for(String unit : mapUnits.keySet()){
				if(name.startsWith(unit)){
					return mapUnits.get(unit).getAddressUnit(name);
				}
			}
		}
		throw new InvalidAddressException("Couldn't find the address "+name+" in this address unit, "+this.name);
	}
	
	public void addAddressValue(String name, AddressValue value){
		synchronized (mapValues) {
			mapValues.put(name, value);
		}
	}
	
	public void addAddressValues(String[] names, AddressValue[] value){
		if(name.length()!= value.length)return;
		synchronized (mapValues) {
			for(int x =0;x<name.length();x++){
				mapValues.put(names[x], value[x]);
			}
		}
	}

	public void removeAddressValue(String name){
		synchronized (mapValues) {
			mapValues.remove(name);
		}
	}

	/***
	 * Method that traverses the full name space after the value name or throws an Invalid Address. 
	 * @param name
	 * @return AddressUnit with the name.
	 */
	public AddressValue getAddressValue(String name) throws InvalidAddressException{
		if(mapValues.containsKey(name)){
			synchronized (mapValues) {
				return mapValues.get(name);
			}
		}else{
			for(String unit : mapUnits.keySet()){
				if(name.startsWith(unit)){
					return mapUnits.get(unit).getAddressValue(name);
				}
			}
		}
		throw new InvalidAddressException("Couldn't find the address "+name+" in this address unit, "+this.name);
	}
	
	public void subscribe(IAddressUnitChanged observer){
		synchronized (changedUnits) {
			observers.add(observer);
		}
	}
	 
	public void unsubscribe(IAddressUnitChanged observer){
		synchronized (changedValues) {
			observers.remove(observer);
		}
	}

	@Override
	public void valueHasChanged(String name, Object value, Date ts) {
		System.out.println("## Fatal addressunit method called not implemented ##");
	}
	
	@Override
	public void unitHasChanged(AddressUnit unit) {
		changedUnits.add(unit);
	}
	
	@Override
	public void unitHasChanged(String address) {
		try{
			changedUnits.add(getAddressUnit(address));
		}catch(InvalidAddressException ex){
		  ex.printStackTrace();	
		}
	}
}
