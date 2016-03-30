package com.gecisyon.timeseries.ingest.simulator.util;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Classe che estende il comportamento standard di un ThreadPoolExecutor, permettendone la messa in pausa
 * @author Fabio Terella -> su esempio trovato su internet
 */
public class PausableThreadPoolExecutor extends ThreadPoolExecutor {

	public PausableThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
	}
	
	public PausableThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	private boolean isPaused;
	private ReentrantLock pauseLock = new ReentrantLock();
	private Condition unpaused = pauseLock.newCondition();   

	protected void beforeExecute(Thread t, Runnable r) {
		super.beforeExecute(t, r);
		pauseLock.lock();
		try{
			while(isPaused) 
				unpaused.await();
		} 
		catch(InterruptedException ie){
			t.interrupt();
		} 
		finally{
			pauseLock.unlock();
		}
    }

	public void pause() {
		pauseLock.lock();
	    try{
	    	isPaused = true;
	    } 
	    finally{
	    	pauseLock.unlock();
	    }
	}
	
	public boolean isPaused(){
		return isPaused;
	}

    public void resume() {
    	pauseLock.lock();
    	try{
    		isPaused = false;
    		unpaused.signalAll();
    	} 
    	finally{
    		pauseLock.unlock();
    	}
   }
	
}

