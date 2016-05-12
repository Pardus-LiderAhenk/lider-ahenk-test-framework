package test.apacheds.server;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.core.api.InstanceLayout;
import org.apache.directory.server.core.factory.DefaultDirectoryServiceFactory;
import org.apache.directory.server.core.partition.impl.avl.AvlPartition;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.store.LdifFileLoader;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.apache.directory.shared.ldap.model.entry.Entry;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EmbeddedLdapServer {
	//Eger partition olusturmak icin 2. yontem (JSON dosyasi okumak) secildiyse bu dosyadan okunabilir.
	//public static final String PARTITIONS_JSON = "/partitions.json";
	private static final String INSTANCE_NAME = "Test";
	private static final String INSTANCE_PATH = "/tmp/ldapServer";
	private static final String BASE_DN = "dc=tr,dc=org,dc=pardus,dc=mys,dc=test";
	private static final String BANNER_LDAP =
	          "           _                     _          ____  ____   \n"
	        	        + "          / \\   _ __    ___  ___| |__   ___|  _ \\/ ___|  \n"
	        	        + "         / _ \\ | '_ \\ / _` |/ __| '_ \\ / _ \\ | | \\___ \\  \n"
	        	        + "        / ___ \\| |_) | (_| | (__| | | |  __/ |_| |___) | \n"
	        	        + "       /_/   \\_\\ .__/ \\__,_|\\___|_| |_|\\___|____/|____/  \n"
	        	        + "               |_|                                       \n";
	private final Logger log = LoggerFactory.getLogger(EmbeddedLdapServer.class);

	private DirectoryService directoryService;
	private LdapServer ldapService;

	private final String host;
	private final Integer port;

	public EmbeddedLdapServer(final String host, final Integer port) {
		this.host = host;
		this.port = port;

		try {
			init();
		} catch (IOException e) {
			log.error("IOException while initializing EmbeddedLdapServer", e);
		} catch (LdapException e) {
			log.error("LdapException while initializing EmbeddedLdapServer", e);
		} catch (NamingException e) {
			log.error("NamingException while initializing EmbeddedLdapServer",
					e);
		} catch (Exception e) {
			log.error("Exception while initializing EmbeddedLdapServer", e);
		}
	}

	private void init() throws Exception, IOException, LdapException,
			NamingException {

		DefaultDirectoryServiceFactory factory = new DefaultDirectoryServiceFactory();
		factory.init(INSTANCE_NAME);

		directoryService = factory.getDirectoryService();
		directoryService.getChangeLog().setEnabled(false);
		directoryService.setShutdownHookEnabled(true);
		
		InstanceLayout il = new InstanceLayout(INSTANCE_PATH);
		directoryService.setInstanceLayout(il);
		
		//Partition eklemek icin 1. yontem
		AvlPartition partition = new AvlPartition(
				directoryService.getSchemaManager());
		partition.setId(INSTANCE_NAME);
		partition.setSuffixDn(new Dn(directoryService.getSchemaManager(),
				BASE_DN));
		partition.initialize();
		directoryService.addPartition(partition);
		//2. yontem ise json dosyasindan okumak 
		/*PartitionHandler ph = new PartitionHandler(directoryService);
        ph.init(PARTITIONS_JSON);*/
		
		ldapService = new LdapServer();
		ldapService.setTransports(new TcpTransport(host, port));
		ldapService.setDirectoryService(directoryService);
		
		
		
	}

	public void start() throws Exception {

		if (ldapService.isStarted()) {
			throw new IllegalStateException("Service already running");
		}

		directoryService.startup();
		ldapService.start();
		System.out.println(BANNER_LDAP);
	}

	public void stop() throws Exception {

		if (!ldapService.isStarted()) {
			throw new IllegalStateException("Service is not running");
		}

		ldapService.stop();
		directoryService.shutdown();
	}

	public void applyLdif(final File ldifFile) throws Exception {
		new LdifFileLoader(directoryService.getAdminSession(), ldifFile, null)
				.execute();
	}

	public void createEntry(final String id,
			final Map<String, String[]> attributes) throws LdapException,
			LdapInvalidDnException {

		if (!ldapService.isStarted()) {
			throw new IllegalStateException("Service is not running");
		}

		Dn dn = new Dn(directoryService.getSchemaManager(), id);
		if (!directoryService.getAdminSession().exists(dn)) {
			Entry entry = directoryService.newEntry(dn);
			for (String attributeId : attributes.keySet()) {
				entry.add(attributeId, attributes.get(attributeId));
			}
			directoryService.getAdminSession().add(entry);
		}
	}
}
