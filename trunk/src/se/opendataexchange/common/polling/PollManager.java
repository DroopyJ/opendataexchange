package se.opendataexchange.common.polling;

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class PollManager {
	
	private ArrayList<PollPool> pollPools;
	private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5);

	public PollManager() {
		super();
	}

	public ArrayList<PollPool> getPollPools() {
		return pollPools;
	}

	public void setPollPools(ArrayList<PollPool> pollPools) {
		this.pollPools = pollPools;
	}
	
	public void init(){
	}
	
	public void start(){
		//When we start the scheduled tasks we give them a random start delay so that they don't have the same start time.
		for(PollPool p:pollPools){
			executor.scheduleAtFixedRate(p, 1000, p.getPollIntervall(),TimeUnit.MILLISECONDS);
		}
	}
	
	public void stop(){
		executor.shutdown();
	}
}
