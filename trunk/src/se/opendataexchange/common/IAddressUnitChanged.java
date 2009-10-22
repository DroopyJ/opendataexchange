package se.opendataexchange.common;

public interface IAddressUnitChanged {

	void unitHasChanged(AddressUnit unit);
	void unitHasChanged(String address);
}
