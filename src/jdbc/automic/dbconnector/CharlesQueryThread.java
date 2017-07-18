package jdbc.automic.dbconnector;

import com.sun.org.apache.regexp.internal.RE;

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
		if(!isEmpty(rs)){
			if(ID){
				try {
					rs.last();
					rs.getInt("id");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean isEmpty(ResultSet resultSet){
		boolean returnvalue = false;
		try {
			if(!resultSet.next()){
                returnvalue= true;
            }
            resultSet.beforeFirst();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnvalue;
	}
}
