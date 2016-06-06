package test.liderahenk.xmpp;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.vysper.mina.TCPEndpoint;
import org.apache.vysper.storage.StorageProviderRegistry;
import org.apache.vysper.storage.inmemory.MemoryStorageProviderRegistry;
import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.addressing.EntityImpl;
import org.apache.vysper.xmpp.authorization.AccountManagement;
import org.apache.vysper.xmpp.modules.Module;
import org.apache.vysper.xmpp.modules.extension.xep0049_privatedata.PrivateDataModule;
import org.apache.vysper.xmpp.modules.extension.xep0050_adhoc_commands.AdhocCommandsModule;
import org.apache.vysper.xmpp.modules.extension.xep0054_vcardtemp.VcardTempModule;
import org.apache.vysper.xmpp.modules.extension.xep0077_inbandreg.InBandRegistrationModule;
import org.apache.vysper.xmpp.modules.extension.xep0092_software_version.SoftwareVersionModule;
import org.apache.vysper.xmpp.modules.extension.xep0119_xmppping.XmppPingModule;
import org.apache.vysper.xmpp.modules.extension.xep0133_service_administration.ServiceAdministrationModule;
import org.apache.vysper.xmpp.modules.extension.xep0202_entity_time.EntityTimeModule;
import org.apache.vysper.xmpp.server.XMPPServer;

/**
 * starts the server as a standalone application
 *
 * @author The Apache MINA Project (dev@mina.apache.org)
 */
public class ServerMain implements IoHandler{

	public static final int SHUTDOWN_PORT = 5333;
	private XMPPServer server;
	private IoAcceptor shutdownAcceptor;
	
	boolean exit = false;
	
	
	public ServerMain() throws Exception {
		server = init();
		shutdownAcceptor = addShutdownHook();
	}
	
    /**
     * boots the server as a standalone application
     * 
     * adding a module from the command line:
     * using a runtime property, one or more modules can be specified, like this:
     * -Dvysper.add.module=org.apache.vysper.xmpp.modules.extension.xep0060_pubsub.PublishSubscribeModule,... more ...
     * 
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
    	ServerMain serverMain = new ServerMain();
    }
    
    public void stopServer(){
    	shutdownAcceptor.unbind();
    	server.stop();
    	//System.exit(0);
    }
    
    
    public XMPPServer init() throws Exception{


        String domain = "localhost";
        
        String addedModuleProperty = System.getProperty("vysper.add.module");
        List<Module> listOfModules = null;
        if (addedModuleProperty != null) {
            String[] moduleClassNames = addedModuleProperty.split(",");
            listOfModules = createModuleInstances(moduleClassNames);
        }

        // choose the storage you want to use
        //StorageProviderRegistry providerRegistry = new JcrStorageProviderRegistry();
        StorageProviderRegistry providerRegistry = new MemoryStorageProviderRegistry();

        final Entity adminJID = EntityImpl.parseUnchecked("lider1@" + domain);
        final AccountManagement accountManagement = (AccountManagement) providerRegistry
                .retrieve(AccountManagement.class);

        String initialPassword = System.getProperty("vysper.admin.initial.password", "admin");
        if (!accountManagement.verifyAccountExists(adminJID)) {
            accountManagement.addUser(adminJID, initialPassword);
            Entity user = EntityImpl.parseUnchecked("user@" + domain);
            accountManagement.addUser(user, "asddsa123");
        }
        
        Entity user = EntityImpl.parseUnchecked("user@" + domain);
        accountManagement.addUser(user, "asddsa123");

        XMPPServer server = new XMPPServer(domain);
        server.addEndpoint(new TCPEndpoint());
        //server.addEndpoint(new StanzaSessionFactory());
        server.setStorageProviderRegistry(providerRegistry);

        URL url = Thread.currentThread().getContextClassLoader().getResource("bogus_mina_tls.cert");
        URI uri = url.toURI();
        
        server.setTLSCertificateInfo(new File(uri), "boguspw");

        try {
            server.start();
            System.out.println("vysper server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }

        server.addModule(new SoftwareVersionModule());
        server.addModule(new EntityTimeModule());
        server.addModule(new VcardTempModule());
        server.addModule(new XmppPingModule());
        server.addModule(new PrivateDataModule());
        server.addModule(new InBandRegistrationModule());
        server.addModule(new AdhocCommandsModule());
        final ServiceAdministrationModule serviceAdministrationModule = new ServiceAdministrationModule();
        // unless admin user account with a secure password is added, this will be not become effective
        serviceAdministrationModule.setAddAdminJIDs(Arrays.asList(adminJID)); 
        server.addModule(serviceAdministrationModule);

        if (listOfModules != null) {
            for (Module module : listOfModules) {
                server.addModule(module);
            }
        }
        
        return server;
    
    }
    
    public IoAcceptor addShutdownHook() throws IOException{
    	//server.stop();
    	IoAcceptor shutdownAcceptor = new NioSocketAcceptor();
        shutdownAcceptor.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new TextLineCodecFactory( Charset.forName( "UTF-8" ))));
        // Configurate the buffer size and the iddle time
        shutdownAcceptor.getSessionConfig().setReadBufferSize( 2048 );
        shutdownAcceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE, 10 );
        shutdownAcceptor.setHandler( this ); 
        shutdownAcceptor.bind(new InetSocketAddress(SHUTDOWN_PORT));
        
        return shutdownAcceptor;
    }

    private static List<Module> createModuleInstances(String[] moduleClassNames) {
        List<Module> modules = new ArrayList<Module>();

        for (String moduleClassName : moduleClassNames) {
            Class<Module> moduleClass;
            try {
                moduleClass = (Class<Module>) Class.forName(moduleClassName);
            } catch (ClassCastException e) {
                System.err.println("not a Vysper module class: " + moduleClassName);
                continue;
            } catch (ClassNotFoundException e) {
                System.err.println("could not load module class " + moduleClassName);
                continue;
            }
            try {
                Module module = moduleClass.newInstance();
                modules.add(module);
            } catch (Exception e) {
                System.err.println("failed to instantiate module class " + moduleClassName);
                continue;
            }
        }
        return modules;
    }
    
//    @Override
	public void sessionOpened(IoSession session) throws Exception {
		System.out
				.println("ServerMain.startServer().new IoHandler() {...}.sessionOpened()");
		
	}
	
//	@Override
	public void sessionIdle(IoSession session, IdleStatus idleStatus) throws Exception {
		System.out
				.println("ServerMain.startServer().new IoHandler() {...}.sessionIdle()");
		
	}
	
//	@Override
	public void sessionCreated(IoSession session) throws Exception {
		System.out
				.println("ServerMain.startServer().new IoHandler() {...}.sessionCreated()");
		
	}
	
//	@Override
	public void sessionClosed(IoSession session) throws Exception {
		System.out
				.println("ServerMain.startServer().new IoHandler() {...}.sessionClosed()");
		
	}
	
//	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		System.out
				.println("ServerMain.startServer().new IoHandler() {...}.messageSent()");
	}
	
//	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		
		String str = message.toString();
		System.out.println( str );
		
		if( str.trim().equalsIgnoreCase("quit")){
			session.close(true);
			exit = true;
			//shutdownAcceptor.unbind();
			//System.exit(0);
		}
	}
	
//	@Override
	public void exceptionCaught(IoSession session, Throwable thowable)
			throws Exception {
		thowable.printStackTrace();
	}
}