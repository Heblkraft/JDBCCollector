package jdbc.automic.dbconnector;

import java.sql.ResultSet;

public class CharlesQueryThread extends Thread{
	DBConnector dbConnector;

	public CharlesQueryThread(String name, DBConnector dbConnector) {
		super(name);
		this.dbConnector = dbConnector;
	}

	@Override
	public void run() {
		ResultSet rs = dbConnector.sendQuery(MainQueryThread.QUERY);
	}
}
