package jdbc.automic.dbconnector;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static jdbc.automic.configuration.ConfigLoader.config;

public class MainQueryThread extends Thread{
	private Logger logger = Logger.getLogger(MainQueryThread.class);
	private final DBConnector dbconnector;
	private final List<CharlesQueryThread> subThreads;
	private boolean killFlag = false;

	/**
	 * <P>Initalizes the Worker Thread Pool</P>
	 * @param dbconnector DbConnector instance is needed
	 */
	public MainQueryThread(DBConnector dbconnector) {
		super("MainThread");
		this.dbconnector = dbconnector;
		subThreads = Collections.synchronizedList(new ArrayList<>());
		initThreadPool(Integer.parseInt(config.get("max.threadpool")));
		logger.debug("Setting Thread-pool-Size to "+ config.get("max.threadpool"));
		logger.debug("Setting Pool-Interval to "+ config.get("poll.interval"));
	}

	/**
	 * <P>Implementation of the runnable interface for the Thread</P>
	 * <P>Starts the Worker Thread each Poll Interval</P>
	 */
	@Override
	public void run() {
		while(!killFlag) {
			startThread();
			try {
				Thread.sleep(Integer.parseInt(config.get("poll.interval")));
				System.gc();
			} catch (InterruptedException e) {
				logger.error("Thread "+ this.getName()+ " is shutting down");
				logger.trace("", e);
			}
		}
	}

	/**
	 * <P>Kills the MainQueryThread</P>
	 */
	private void killThread() {
		this.killFlag = true;
		try {
			this.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logger.debug("Shutting down Thread: "+ this.getName());
	}

	/**
	 * <P>Starts a thread which is not alive and replaces the old thread with a new one</P>
	 */
	private void startThread(){
		for(CharlesQueryThread thread : subThreads){
			if(!thread.isAlive()){
				try {
				thread.start();
				} catch (IllegalThreadStateException e){
					subThreads.set(subThreads.indexOf(thread),new CharlesQueryThread(thread.getName(), dbconnector));
					logger.debug("Replacing Thread: "+thread.getName()+ " with new Thread");
				}
				return;
			}
		}
	}

	/**
	 * <P>Initalizes the Thread Pool with a given pool size</P>
	 * @param number Number of threads in the thread Pool
	 */
	private void initThreadPool(int number) {
		logger.debug("Initalizing Thread Pool");
		for(int i = 0; i<number; i++) {
			subThreads.add(new CharlesQueryThread("SubThread #"+i, dbconnector));
		}
	}
}
