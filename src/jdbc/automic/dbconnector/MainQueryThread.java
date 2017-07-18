package jdbc.automic.dbconnector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainQueryThread extends Thread{
	private static final int POLL_TIME = 1000;
	
	private DBConnector dbconnector;
	private List<CharlesQueryThread> subThreads;
	private boolean killFlag = false;
	
	public MainQueryThread(DBConnector dbconnector) {
		super("MainThread");
		this.dbconnector = dbconnector;
		subThreads = Collections.synchronizedList(new ArrayList<>());
		initThreadPool(10);
		startThread();
	}

	@Override
	public void run() {
		while(killFlag) {

			try {
				this.sleep(POLL_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void killThread() {
		this.killFlag = true;
	}

	private void startThread(){
		for(CharlesQueryThread thread : subThreads){
			System.out.println(thread.getName());
		}
	}
	
	private void initThreadPool(int number) {
		for(int i = 0; i<number; i++) {
			subThreads.add(new CharlesQueryThread("SubThread #"+i));
		}
	}
}
