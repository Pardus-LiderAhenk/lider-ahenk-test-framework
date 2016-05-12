package test.apacheds;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchResult;
import test.apacheds.server.EmbeddedLdapServer;

public class Main {

	private static final String HOST = "localhost";
	private static final Integer PORT = 10389;
	public static final String EXAMPLE_LDAP_DATA = "/ldap-example-data.ldif";
	public static final String PARDUS_MYS_LDAP_SCHEMA = "/pardus-mys-schema.ldif";
	
	public static void main(final String[] args) {
		Main main = new Main();
		main.execute();
		System.exit(0);
	}

	private void execute() {
		// start server
		System.out.println("Initializing embedded LDAP server");
		EmbeddedLdapServer embeddedLdapServer = new EmbeddedLdapServer(HOST,
				PORT);
		try {
			embeddedLdapServer.start();

			// Load the directory as a resource
			
			File dir = new File(this.getClass()
                    .getResource(EXAMPLE_LDAP_DATA).getFile());
			
			embeddedLdapServer.applyLdif(dir);

			dir = new File(this.getClass()
                    .getResource(PARDUS_MYS_LDAP_SCHEMA).getFile());
			embeddedLdapServer.applyLdif(dir);

			System.out.println("Embedded LDAP server started");			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				//stop server
				embeddedLdapServer.stop();
				System.out.println("Embedded LDAP server stopped");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
