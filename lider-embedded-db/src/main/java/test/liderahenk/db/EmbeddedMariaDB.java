package test.liderahenk.db;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;

public class EmbeddedMariaDB {
	
	private Integer port = 3306;
	private String dataPath = "/tmp/db";
	private String dbName = "liderdb";
	private DBConfigurationBuilder configBuilder;
	private DB db;
	
	public void init(){
		setConfigBuilder(DBConfigurationBuilder.newBuilder());
		getConfigBuilder().setPort(getPort());
		getConfigBuilder().setDataDir(getDataPath());
		try {
			setDb(DB.newEmbeddedDB(getConfigBuilder().build()));
		} catch (ManagedProcessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void start(){
		if(getDb() != null){
			try {
				getDb().start();
			} catch (ManagedProcessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void stop() {
		if(getDb() != null){
			try {
				getDb().stop();
			} catch (ManagedProcessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getDataPath() {
		return dataPath;
	}

	public void setDataPath(String dataPath) {
		this.dataPath = dataPath;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public DBConfigurationBuilder getConfigBuilder() {
		return configBuilder;
	}

	public void setConfigBuilder(DBConfigurationBuilder configBuilder) {
		this.configBuilder = configBuilder;
	}

	public DB getDb() {
		return db;
	}

	public void setDb(DB db) {
		this.db = db;
	}

}
