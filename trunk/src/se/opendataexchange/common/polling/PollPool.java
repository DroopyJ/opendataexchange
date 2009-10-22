package se.opendataexchange.common.polling;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;



public class PollPool implements Runnable {
	
	private ArrayList<IPolledData> data2Poll;
	private long pollIntervall;
	private ArrayList<String> errors = new ArrayList<String>();
	private String[] pollableItems;
	private IPollData dataSource=null;
	private static Logger l = Logger.getLogger(PollPool.class);
	private String name;
	ReentrantLock lock;
	public PollPool() {
		super();
		lock = new ReentrantLock();
	}

	public void init(){

	}
	
	public ArrayList<IPolledData> getData2Poll() {
		return data2Poll;
	}

	public void setData2Poll(ArrayList<IPolledData> data2Poll) {
		this.data2Poll = data2Poll;
		synchronized (this.data2Poll) {
			//Copy to array to allow faster handling when polling.
			pollableItems = new String[data2Poll.size()];
			int x = 0;
			for(IPolledData pd:data2Poll){
				pollableItems[x]=pd.getPollName();
				x++;
			}
		}
	}

	public long getPollIntervall() {
		return pollIntervall;
	}

	public void setPollIntervall(long pollIntervall) {
		this.pollIntervall = pollIntervall;
	}
	
	public IPollData getDataSource() {
		return dataSource;
	}

	public void setDataSource(IPollData dataSource) {
		this.dataSource = dataSource;
	}

	public void resetErrorList(){
		errors.clear();
	}
	
	public ArrayList<String> getErrors(){
		return errors;
	}
	long lastTs = 0;
	long maxDiff = 0;
	long minDiff = Long.MAX_VALUE;
	@Override
	public void run() {
		try{
			if(dataSource!=null){
				if(lock.tryLock(pollIntervall/2, TimeUnit.MILLISECONDS)){
					Date timestamp = new Date(System.currentTimeMillis());
					if (timestamp.getTime() != lastTs){
						Object[] datas = dataSource.readData(pollableItems);
						if (timestamp.getTime()-lastTs > maxDiff && (lastTs>0)){
							maxDiff = timestamp.getTime()-lastTs;
							System.out.println(name +" ######## New max:" +maxDiff +" ############");
						}
						if (timestamp.getTime()-lastTs < minDiff && (lastTs>0)){
							minDiff = timestamp.getTime()-lastTs;
							System.out.println(name +" ######## New min:" +minDiff +" ############");
						}
						lastTs = timestamp.getTime();
						if(datas != null){
							int x=0;
							for(Object data:datas){
								data2Poll.get(x).setValue(data,timestamp);
								x++;
							}
							x++;
						}
					}
					else
					{
						System.out.print(".");
					}
				}
				else
				{
					System.out.println(" ######## Missed lock ############");
				}				
			}
		}catch(Exception ex){;
			ex.printStackTrace();
			l.error(ex);
			ex.printStackTrace();
		}finally{
			if(lock.isLocked() && lock.isHeldByCurrentThread())
				lock.unlock();
		}
		
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
}
