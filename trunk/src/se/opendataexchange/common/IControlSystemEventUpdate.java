package se.opendataexchange.common;

public interface IControlSystemEventUpdate {
	
	void newEvent(String[] names, Object[] values);
	
	void addAddressSpace(AddressSpace space);
	
	void addToAddressSpace(String address,AddressUnit unitToAdd) throws InvalidAddressException;
	
	void addToAddressUnit(String address, AddressValue valueToAdd) throws InvalidAddressException;
	
	void removeFromAddressSpace(String address) throws InvalidAddressException;

}
