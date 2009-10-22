package se.opendataexchange.common;

import java.util.ArrayList;
import java.util.HashMap;

public class AddressSpace {
	
	private HashMap<String,AddressUnit> mapUnits = new HashMap<String, AddressUnit>();
	private HashMap<String,AddressValue> mapValues = new HashMap<String, AddressValue>();
	
	private String name;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	
	public HashMap<String, AddressValue> getMapValues() {
		return mapValues;
	}

	public void setMapValues(HashMap<String, AddressValue> mapValues) {
		this.mapValues = mapValues;
	}

	public HashMap<String, AddressUnit> getMapUnits() {
		return mapUnits;
	}

	public void setMapUnits(HashMap<String, AddressUnit> mapUnits){
		this.mapUnits=mapUnits;
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
		throw new InvalidAddressException("Couldn't find the address "+name+" in this address space, "+this.name);
	}
	/***
	 * Method that returns all existing address units at top level.
	 * @return array of address units.
	 */
	public AddressUnit[] getAddressUnits(){
		AddressUnit[] units = new AddressUnit[mapUnits.values().size()];
		synchronized (mapUnits) {
			mapUnits.values().toArray(units);
		}
		return units;
	}
	
	public void addAddressValue(String name, AddressValue value){
		synchronized (mapValues) {
			mapValues.put(name, value);
		}
	}
	public void addAddressValue(String[] names, AddressValue[] value){
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
		throw new InvalidAddressException("Couldn't find the address "+name+" in this address space, "+this.name);
	}
	
	public AddressValue[] getAddressValues(){
		AddressValue[] values = new AddressValue[mapValues.values().size()];
		synchronized (mapValues) {
			mapValues.values().toArray(values);
		}
		return values;
	}
	
	public ArrayList<String> getAddresses(){
		ArrayList<String> result = new ArrayList<String>();
		for(AddressValue val : mapValues.values()){
			result.add(val.getName());
		}
		for(AddressUnit unit : mapUnits.values()){
			result.addAll(getAddresses(unit));
		}
		return result;
	}
	
	private ArrayList<String> getAddresses(AddressUnit unit2){
		ArrayList<String> result = new ArrayList<String>();
		for(AddressValue val : unit2.getMapValues().values()){
			result.add(val.getName());
		}
		for(AddressUnit unit : unit2.getMapUnits().values()){
			result.addAll(getAddresses(unit));
		}
		return result;
	}
	
	public HashMap<String, Object> getAllValues() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		for(AddressValue val : mapValues.values())
			map.put(val.getName(), val.getValue());
		for(AddressUnit unit : mapUnits.values())
			map.putAll(getAllValues(unit));
		return map;
	}
	
	public HashMap<String, Object> getAllValues(AddressUnit unit2) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		for(AddressValue val : unit2.getMapValues().values())
			map.put(val.getName(), val.getValue());
		for(AddressUnit unit : unit2.getMapUnits().values())
			map.putAll(getAllValues(unit));
		return map;
	}
	
	public void subscribeForAddressValue(String addressName, IAddressValueChanged observer) throws InvalidAddressException{
			getAddressValue(addressName).subscribe(observer);
	}
	
	public void unsubscribeForAddressValue(String addressName, IAddressValueChanged observer) throws InvalidAddressException{
		getAddressValue(addressName).unsubscribe(observer);
	}
	
	public void subscribeForAddressUnit(String addressName, IAddressUnitChanged observer) throws InvalidAddressException{
		getAddressUnit(addressName).subscribe(observer);
	}
	
	public void unsubscribeForAddressUnit(String addressName, IAddressUnitChanged observer) throws InvalidAddressException{
		getAddressUnit(addressName).unsubscribe(observer);
	}
}
