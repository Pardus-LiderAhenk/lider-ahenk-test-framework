package test.liderahenk.ldap;

import test.liderahenk.ldap.server.EmbeddedLdapServer;

public class LDAPExecutor extends Thread{
	
	EmbeddedLdapServer server;
	
	@Override
	public void run() {
		server = new EmbeddedLdapServer();
		server.execute();
	}
	
	public void stopLdap(){
		
		server.keepRun = false;
		
	}

}
