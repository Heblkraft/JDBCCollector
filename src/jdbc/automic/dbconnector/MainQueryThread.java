package jdbc.automic.dbconnector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainQueryThread extends Thread{
	private static final int POLL_TIME = 2000;
	protected static final String QUERY = "select * from test_table_id";
	
	private DBConnector dbconnector;
	private List<CharlesQueryThread> subThreads;
	private boolean killFlag = false;
	
	public MainQueryThread(DBConnector dbconnector) {
		super("MainThread");
		this.dbconnector = dbconnector;
		subThreads = Collections.synchronizedList(new ArrayList<>());
		initThreadPool(10);
	}

	@Override
	public void run() {
		System.out.println("MainThread Running");
		while(!killFlag) {
			startThread();
			try {
				Thread.sleep(POLL_TIME);
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
			if(!thread.isAlive()){
				try {
				thread.start();
				} catch (IllegalThreadStateException e){
					subThreads.set(subThreads.indexOf(thread),new CharlesQueryThread(thread.getName(), dbconnector));
				}
				return;
			}
		}
	}

	
	private void initThreadPool(int number) {
		for(int i = 0; i<number; i++) {
			subThreads.add(new CharlesQueryThread("SubThread #"+i, dbconnector));
		}
	}
}
