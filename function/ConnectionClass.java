package BoardUser.function;


import java.sql.*;

public class ConnectionClass {
	
	static	Connection  conn = null;
	
	public static Connection connection() {
			
			
			try {
				Class.forName("oracle.jdbc.OracleDriver");
				
					conn = 
							DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","system","oracle");
					
				
					
			}catch (SQLException e) {
				System.out.println("connection() 에러");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return conn; 
			
			
	}
	public static void connectionLogout(Connection c) {
			
		if(c!=null) {
			try {
				conn.close();
			} catch (SQLException e) {
				System.out.println("connectionLogout(Connection c) 에러");
			}
		}
	}
}