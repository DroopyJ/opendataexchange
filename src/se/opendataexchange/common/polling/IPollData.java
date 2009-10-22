package se.opendataexchange.common.polling;

public interface IPollData {

	Object[] readData(String[] items);
	Object[] readData(String item,int arraySize);
	Object readData(String item);
	
	void writeData(String[] items,Object[] datas);
	void writeData(String item, Object data);
	void writeData(String item, Object[] datas);
	
}
