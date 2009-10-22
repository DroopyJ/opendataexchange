package se.opendataexchange.common.polling;

import java.util.Date;

public interface IPolledData {
	
	public String getPollName(); 

	public void setPollName(String name); 

	public Object getValue();
	
	public void setValue(Object value,Date timestamp);
	
}
