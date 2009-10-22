package se.opendataexchange.common.polling;

import java.util.Date;

import se.opendataexchange.common.AddressValue;

public class PolledAddressValue extends AddressValue implements IPolledData {

	private String pollName;
	
	@Override
	public void setValue(Object value, Date timestamp) {
		newData(getName(), value, timestamp);
		//System.out.println("Got value "+getName()+" from PLC:"+value);
	}

	@Override
	public void setPollName(String name) {
		this.pollName=name;
		
	}

	@Override
	public String getPollName() {
		return this.pollName;
	}
	
	
}
