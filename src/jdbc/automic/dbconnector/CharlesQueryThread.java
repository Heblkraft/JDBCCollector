package jdbc.automic.dbconnector;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CharlesQueryThread extends Thread{
	DBConnector dbConnector;

	public CharlesQueryThread(String name, DBConnector dbConnector) {
		super(name);
		this.dbConnector = dbConnector;
	}

	@Override
	public void run() {
		ResultSet rs = dbConnector.sendQuery(MainQueryThread.QUERY);
		if(!isEmpty(rs)){

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
