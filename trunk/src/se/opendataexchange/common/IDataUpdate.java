package se.opendataexchange.common;

import java.util.Date;

public interface IDataUpdate {
	
	void newData(String[] names, Object[] values, Date timestamp);
	void newData(String name, Object value,Date timestamp);
	void newData(Object value,Date timestamp);

}
