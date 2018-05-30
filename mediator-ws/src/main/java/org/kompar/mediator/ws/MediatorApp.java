package org.komparator.mediator.ws;

import org.komparator.security.SecuritySingleton;

public class MediatorApp {

	public static void main(String[] args) throws Exception {
		// Check arguments
		if (args.length == 0 || args.length == 2) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + MediatorApp.class.getName() + " wsURL OR uddiURL wsName wsURL");
			return;
		}

		String uddiURL = null;
		String wsName = null;
		String wsURL = null;
		;
		String wsI = null;

		// Create server implementation object, according to options
		MediatorEndpointManager endpoint = null;



		if (args.length == 1) {
			wsURL = args[0];
			endpoint = new MediatorEndpointManager(wsURL);
		} else if (args.length >= 3) {
			uddiURL = args[0];
			wsName = args[1];
			wsURL = args[2];
			wsI = args[3];
			SecuritySingleton sec = SecuritySingleton.getInstance();
			sec.setName(wsName);
			sec.setWsI(Integer.parseInt(wsI));
			endpoint = new MediatorEndpointManager(uddiURL, wsName, wsURL, wsI);
			endpoint.setVerbose(true);
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				String wsName = args[1];
				System.out.println("Unpublished" + wsName + "from UDDI");
			}
		});

		LifeProof lifeProof = new LifeProof(endpoint);
		try {
			endpoint.start();
			lifeProof.start();
			endpoint.awaitConnections();
		} finally {
			endpoint.stop();
		}

	}

}
