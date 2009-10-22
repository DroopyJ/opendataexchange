package se.opendataexchange.common;

import java.util.Date;

public interface IAddressValueChanged {
	
	void valueHasChanged(String name, Object value, Date timestamp);

}
