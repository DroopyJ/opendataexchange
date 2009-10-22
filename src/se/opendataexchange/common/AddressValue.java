package se.opendataexchange.common;

import java.util.Date;
import java.util.HashSet;

public abstract class AddressValue implements IDataUpdate, IAddressValueChanged{

	protected Object value;
	protected Date timestamp;
	private String name;
	private String path;
	private HashSet<IAddressValueChanged> observers = new HashSet<IAddressValueChanged>();
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void subscribe(IAddressValueChanged observer){
		synchronized (observers) {
			observers.add(observer);
		}
	}
	
	public void unsubscribe(IAddressValueChanged observer){
		synchronized (observers) {
			observers.remove(observer);
		}
	}
	
	@Override
	public void newData(String[] names, Object[] values, Date timestamp) {
		if(names.length!=values.length) return;
		for(Object value : values){
			newData(value, timestamp);
		}		
	}

	@Override
	public void newData(String name,Object value, Date timestamp){
		newData(value, timestamp);
	}

	/*@SuppressWarnings("unchecked")
	@Override
	public void newData(Object value, Date timestamp) {
		this.timestamp=timestamp;
		this.value=value;
		if(observers.size() > 0){
			final HashSet<IAddressValueChanged> lstObservers = observers;
			final AddressValue val = this;
			Thread updater = new Thread(new Runnable(){
				public void run(){
					synchronized (lstObservers) {
						for(IAddressValueChanged observer : lstObservers){
							observer.valueHasChanged(val);
						}
					}
				}
			});
			updater.start();
		}
	}*/
	
	@Override
	public void newData(Object valuex, Date timestamp) {
		//TODO: Ugly, redo and generalize when there's time
		if(this.value != null && (name.equals("TPC Logger/Process Right/Cooling water flow") || name.equals("TPC Logger/Process Left/Cooling water flow"))
				&& ((((Double)value)-((Double)this.value) > 5.0*0.33 || (Double)value >= 9.99*0.33 )&& (timestamp.getTime()-this.timestamp.getTime()) < 3000))
			return;
		
		this.value=valuex;
		this.timestamp=timestamp;
		if(observers.size() > 0){
			Updater updater;
			synchronized(observers){
				updater = new Updater(this.name, timestamp.getTime(), valuex, observers);
			}
			updater.start();
		}
	}
	
	private class Updater extends Thread{
		private HashSet<IAddressValueChanged> lstObservers;
		private String name;
		private long ts;
		private Object val;
		public Updater(String name, long ts1, Object val1, HashSet<IAddressValueChanged> obs1){
			this.ts = ts1;
			this.val = val1;
			this.lstObservers = obs1;
			this.name = name;
		}
		public void run(){
			synchronized (lstObservers) {
				for(IAddressValueChanged observer : lstObservers){
					observer.valueHasChanged(name, val, new Date(ts));
				}
			}
		}
	}
	
	@Override
	public void valueHasChanged(String name, Object value, Date timestamp) {
	}
	
	public void setSubscription(AddressValue val){
		val.subscribe(this);
	}
}
