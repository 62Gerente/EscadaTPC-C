package escada.tpc.common.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import escada.tpc.tpcc.database.populate.jmx.DatabasePopulate;

public class DatabaseResources {

	private static final Logger logger = Logger.getLogger(DatabaseResources.class);
	
	public static final String DEFAULT_NUMBER_WAREHOUSES = "4";
	public static final String DEFAULT_DRIVER = "org.postgresql.Driver";
	public static final String DEFAULT_JDBC_CONN_STRING = "jdbc:postgresql://localhost/tpcc";
	public static final String DEFAULT_USER_NAME = "refmanager";
	public static String DEFAULT_PASSWORD = "";	
		
	private String driver = DEFAULT_DRIVER;
	private String connString = DEFAULT_JDBC_CONN_STRING;
	private String userName = DEFAULT_USER_NAME;
	private String password = DEFAULT_PASSWORD;

	public synchronized String getDriver() {
		return driver;
	}

	public synchronized void setDriver(String driver) {
		this.driver = driver;
	}


	public synchronized String getConnString() {
		return connString;
	}


	public synchronized void setConnString(String connString) {
		this.connString = connString;
	}


	public synchronized String getUserName() {
		return userName;
	}


	public synchronized void setUserName(String userName) {
		this.userName = userName;
	}


	public synchronized String getPassword() {
		return password;
	}


	public synchronized void setPassword(String password) {
		this.password = password;
	}


	public DatabaseResources() {
		
		InputStream inStream = DatabasePopulate.class.getResourceAsStream("/database-config.properties");
		Properties props = new Properties();
		try {
			props.load(inStream);
		} catch (IOException e) {
			logger.warn("Unable to load properties from file (database-config.properties). Using defaults!", e);
		}
		
		this.connString = props.getProperty("db.connection.string", DEFAULT_JDBC_CONN_STRING);
		this.driver = props.getProperty("db.driver", DEFAULT_DRIVER);
		this.userName = props.getProperty("db.username", DEFAULT_USER_NAME);
		this.password = props.getProperty("db.password", DEFAULT_PASSWORD);
		
	}
	
}
