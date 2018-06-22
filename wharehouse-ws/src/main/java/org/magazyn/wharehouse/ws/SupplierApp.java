package org.magazyn.wharehouse.ws;

//import org.kompar.security.SecuritySingleton;

/** Main class that starts the Supplier Web Service. */
public class SupplierApp {

	public static void main(String[] args) throws Exception {
		// Check arguments
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + SupplierApp.class.getName() + " wsURL");
			return;
		}
		SupplierEndpointManager endpoint;
		if(args.length == 1){
			String wsURL = args[0];
			endpoint = new SupplierEndpointManager(wsURL);
		}
		else{
			String UDDIURL = args[0];
			String wsName = args[1];
			String wsURL = args[2];
	//		SecuritySingleton sec = SecuritySingleton.getInstance();
	//		sec.setName(wsName);
			endpoint = new SupplierEndpointManager(wsURL,UDDIURL,wsName);
		}
		// Create server implementation object
		
		
		try {
			endpoint.start();
			endpoint.awaitConnections();
		} finally {
			endpoint.stop();
		}

	}

}
