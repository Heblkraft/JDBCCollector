package jdbc.automic.dbconnector;

import com.sun.org.apache.regexp.internal.RE;
import jdbc.automic.restconnector.IRestAction;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CharlesQueryThread extends Thread{
	DBConnector dbConnector;
	private static final boolean ID = true;

	public CharlesQueryThread(String name, DBConnector dbConnector) {
		super(name);
		this.dbConnector = dbConnector;
	}

	@Override
	public void run() {
		ResultSet rs = dbConnector.sendQuery(MainQueryThread.QUERY);
		if(rs == null) System.out.println(currentThread().getName()+": Resultset == null");
		try {
			System.out.println(currentThread().getName()+" : "+IRestAction.fetchData(rs));
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
